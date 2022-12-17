package eu.kanade.tachiyomi.network.interceptor

import eu.kanade.tachiyomi.network.GET
import okhttp3.OkHttpClient
import okio.buffer
import okio.sink
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FilenameFilter
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class NodejsManager {
    companion object {
        private const val NODEJS_VERSION = "v18.12.1"
        private const val NODEJS_URL = "https://nodejs.org/dist/$NODEJS_VERSION"
    }

    enum class SystemType {
        /* GNU */ LINUX,
        MAC,
        WINDOWS
    }

    // Gets system type on demand
    private val system by lazy {
        val systemString = System.getProperty("os.name")
            .split(" ")
            .first()
            .uppercase()
        runCatching { SystemType.valueOf(systemString) }
            .getOrDefault(SystemType.LINUX)
    }

    fun getNodejsPath(env: Map<String, String>): String? {
        val customNodePath = env.get(CustomDriver.PLAYWRIGHT_NODEJS_PATH)
            ?: System.getenv(CustomDriver.PLAYWRIGHT_NODEJS_PATH)
            ?: System.getProperty(CustomDriver.PLAYWRIGHT_PROP_NODEJS_PATH)

        if (customNodePath != null) return customNodePath

        // Let's try to find nodejs in PATH environment variable
        // ... or PATHEXT
        val pathSeparator = System.getProperty("path.separator")
        val envPath = System.getenv("PATH") ?: System.getenv("PATHEXT")!!

        val nodeFilter = object : FilenameFilter {
            override fun accept(dir: File, name: String): Boolean =
                name.equals("node") || name.equals("node.exe")
        }
        envPath.split(pathSeparator).forEach {
            val results = File(it).list(nodeFilter)
            if (results != null && results.size > 0)
                return "$it/${results.first()}"
        }

        // If no custom binary has been defined and the node
        // is not found in the PATH, we have to install it
        return installNodejs()
    }

    private fun installNodejs(): String? {
        val cacheDir = getNodeCacheDir()
        if (!Files.isDirectory(cacheDir.toPath()))
            cacheDir.mkdirs()

        // "normalized" == "something that exists on nodejs.org"
        val normalizedArch = when (System.getProperty("os.arch")) {
            "amd64", "x86_64" -> "x64"
            "arm" -> "armv7l"
            "aarch64" -> "arm64"
            else -> "x86"
        }

        val filename = "node-$NODEJS_VERSION-" + when (system) {
            SystemType.LINUX -> "linux-$normalizedArch.tar.gz"
            SystemType.MAC -> "darwin-$normalizedArch.tar.gz"
            SystemType.WINDOWS -> "win-$normalizedArch.zip"
        }

        val nodebin = if (system == SystemType.WINDOWS) "node.exe" else "node"

        // if its already installed, then just return the installed binary path
        val installedBinary = File(cacheDir, nodebin).toPath()
        if (Files.isRegularFile(installedBinary))
            return installedBinary.toString()

        // Lets download it!
        val downloadedFile = File(cacheDir, filename)
        downloadedFile.deleteOnExit()
        // If for some reason the downloaded file was not deleted
        // OR someone downloaded it externally, then dont try downloading again
        var fileExists = Files.isRegularFile(downloadedFile.toPath())
        if (!fileExists) {
            fileExists = runCatching {
                download("$NODEJS_URL/$filename", downloadedFile)
                true
            }.getOrDefault(false)
        }

        if (fileExists) {
            extractNode(downloadedFile.toPath(), installedBinary)
            return installedBinary.toString()
        }

        return null
    }

    private fun extractNode(targetPath: Path, outputBinary: Path) {
        // if its a .zip, then it is a windows file
        if (targetPath.toString().endsWith(".zip")) {
            FileSystems.newFileSystem(targetPath, emptyMap<String, String>()).use {
                val dirName = targetPath.toString()
                    .substringBefore(".zip")
                    .substringAfterLast("/")
                val path = it.getPath("$dirName/node.exe")
                Files.copy(path, outputBinary)
            }
        } else { // it's time for .tar.gz to shine, linux/mac
            val tarinput = TarArchiveInputStream(
                GzipCompressorInputStream(
                    BufferedInputStream(
                        Files.newInputStream(targetPath)
                    )
                )
            )
            var archive: ArchiveEntry?
            while (tarinput.getNextEntry().also { archive = it } != null) {
                if (archive!!.name.endsWith("node")) {
                    Files.copy(tarinput, outputBinary)
                    outputBinary.toFile().setExecutable(true, true)
                    break
                }
            }
            tarinput.close()
        }
    }

    private fun getNodeCacheDir() = File(getCacheDir(), "node-$NODEJS_VERSION")

    fun getCacheDir(): File {
        // If nothing works, then explode in flames (NPE)
        val home = System.getProperty("user.home")
            ?: System.getProperty("os.home")!!

        // Based on playwright-core/src/server/registry/index.ts
        val cachePath = when (system) {
            SystemType.LINUX -> System.getenv("XDG_CACHE_HOME")
                ?.let { File(it) }
                ?: File(home, ".cache")
            SystemType.WINDOWS ->
                System.getenv("LOCALAPPDATA")
                    ?.let { File(it) }
                    ?: File(File(home, "AppData"), "Local")
            SystemType.MAC -> File(File(home, "Library"), "Caches")
        }
        return File(cachePath, "ms-playwright")
    }

    private fun download(url: String, output: File) {
        val client = OkHttpClient.Builder().build()

        val response = client.newCall(GET(url)).execute()

        // i love okio.
        response.body.source().use { source ->
            output.sink().buffer().use {
                it.writeAll(source)
                it.flush()
            }
        }
    }
}
