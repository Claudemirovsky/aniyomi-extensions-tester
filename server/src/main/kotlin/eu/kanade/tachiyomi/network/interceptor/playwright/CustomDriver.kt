package eu.kanade.tachiyomi.network.interceptor.playwright

import com.microsoft.playwright.impl.driver.Driver
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemAlreadyExistsException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class CustomDriver : Driver() {
    companion object {
        const val PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD"
        const val PLAYWRIGHT_NODEJS_PATH = "PLAYWRIGHT_NODEJS_PATH"
        const val SELENIUM_REMOTE_URL = "SELENIUM_REMOTE_URL"

        const val PLAYWRIGHT_PROP_NODEJS_PATH = "playwright.nodejs.path"

        @JvmStatic
        private fun platformDir(): String {
            val name = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()
            return when {
                name.contains("windows") -> "win32_x64"
                name.contains("linux") -> {
                    when (arch) {
                        "aarch64" -> "linux-arm64"
                        else -> "linux"
                    }
                }
                name.contains("mac os x") -> "mac"
                else -> throw RuntimeException("Unexpected os.name value: $name")
            }
        }

        @JvmStatic
        private fun isExecutable(filePath: Path): Boolean {
            val name = filePath.getFileName().toString()
            return name.endsWith(".sh") || name.endsWith(".exe") || !name.contains(".")
        }
    }

    private val driverTempDir by lazy {
        val alternativeTmpdir = System.getProperty("playwright.driver.tmpdir")
        val prefix = "playwright-java-"
        if (alternativeTmpdir == null) {
            Files.createTempDirectory(prefix)
        } else {
            Files.createTempDirectory(Paths.get(alternativeTmpdir), prefix)
        }.also { it.toFile().deleteOnExit() }
    }

    private var preinstalledNodePath: Path? = null

    init {
        val nodePath = System.getProperty(PLAYWRIGHT_PROP_NODEJS_PATH)
        nodePath?.let {
            preinstalledNodePath = Paths.get(it)
            if (!Files.exists(preinstalledNodePath)) {
                throw RuntimeException("Invalid Node.js path: $nodePath")
            }
            env.put(PLAYWRIGHT_NODEJS_PATH, it)
        }
    }

    @Throws(Exception::class)
    protected override fun initialize(installBrowsers: Boolean) {
        if (preinstalledNodePath == null && env.containsKey(PLAYWRIGHT_NODEJS_PATH)) {
            preinstalledNodePath = Paths.get(env.get(PLAYWRIGHT_NODEJS_PATH))
            if (!Files.exists(preinstalledNodePath)) {
                throw RuntimeException("Invalid Node.js path specified: " + preinstalledNodePath)
            }
        } else if (preinstalledNodePath != null) {
            // Pass the env variable to the driver process.
            env.put(PLAYWRIGHT_NODEJS_PATH, preinstalledNodePath.toString())
        }
        extractDriverToTempDir()
        logMessage("extracted driver from jar to " + driverPath())
        if (installBrowsers) installBrowser(env)
    }

    private fun initFileSystem(uri: URI): FileSystem? {
        return try {
            FileSystems.newFileSystem(uri, emptyMap<String, String>())
        } catch (e: FileSystemAlreadyExistsException) {
            null
        }
    }

    private fun extractDriverToTempDir() {
        val classloader = this::class.java.classLoader
        println("driver/${platformDir()}")
        val originalUri = classloader.getResource(
            "driver/" + platformDir(),
        ).toURI()
        val uri = maybeExtractNestedJar(originalUri)

        // Create zip filesystem if loading from jar.
        initFileSystem(uri)?.use {
            val srcRoot = Paths.get(uri)
            // jar file system's .relativize gives wrong results when used with
            // spring-boot-maven-plugin, convert to the default filesystem to
            // have predictable results.
            // See https://github.com/microsoft/playwright-java/issues/306
            val srcRootDefaultFs = Paths.get(srcRoot.toString())
            for (fromPath in Files.walk(srcRoot)) {
                if (preinstalledNodePath != null) {
                    val fileName = fromPath.getFileName().toString()
                    if ("node.exe".equals(fileName) || "node".equals(fileName)) {
                        continue
                    }
                }
                val relative = srcRootDefaultFs.relativize(
                    Paths.get(fromPath.toString()),
                )
                val toPath = driverTempDir.resolve(relative.toString())
                try {
                    if (Files.isDirectory(fromPath)) {
                        Files.createDirectories(toPath)
                    } else {
                        Files.copy(fromPath, toPath)
                        if (isExecutable(toPath)) {
                            toPath.toFile().setExecutable(true, true)
                        }
                    }
                    toPath.toFile().deleteOnExit()
                } catch (e: IOException) {
                    throw RuntimeException("Failed to extract driver from $uri, full uri: $originalUri", e)
                }
            }
        }
    }

    private fun maybeExtractNestedJar(uri: URI): URI {
        if (!"jar".equals(uri.getScheme())) {
            return uri
        }

        val JAR_URL_SEPARATOR = "!/"
        val parts = uri.toString().split(JAR_URL_SEPARATOR)
        if (parts.size != 3) {
            return uri
        }

        val jarUri = URI("${parts[0]}$JAR_URL_SEPARATOR${parts[1]}")
        initFileSystem(jarUri)!!.use {
            try {
                val fromPath = Paths.get(jarUri)
                val toPath = driverTempDir.resolve(
                    fromPath.getFileName().toString(),
                )
                Files.copy(fromPath, toPath)
                toPath.toFile().deleteOnExit()
                val uriStr = "jar:${toPath.toUri()}$JAR_URL_SEPARATOR${parts[2]}"
                return URI(uriStr)
            } catch (e: IOException) {
                throw RuntimeException("Failed to extract driver's nested .jar from " + jarUri + "; full uri: " + uri, e)
            }
        }
    }

    private fun installBrowser(env: Map<String, String>) {
        val skip = PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD.let {
            env.get(it) ?: System.getenv(it) ?: System.getProperty(it)
        }
        if (skip != null && "0" != skip && "false" != skip) {
            logMessage("Skipping browsers download because `$PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD` env variable is set")
            return
        }

        if (env.get(SELENIUM_REMOTE_URL) != null || System.getenv(SELENIUM_REMOTE_URL) != null) {
            logMessage("Skipping browsers download because `SELENIUM_REMOTE_URL` env variable is set")
            return
        }
        val driver = driverPath()
        if (!Files.exists(driver)) {
            throw RuntimeException("Failed to find driver: $driver")
        }

        val pb = createProcessBuilder()
        pb.command().add("install")
        pb.redirectError(ProcessBuilder.Redirect.INHERIT)
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT)

        val process = pb.start()
        val result = process.waitFor(10, TimeUnit.MINUTES)
        if (!result) {
            process.destroy()
            throw RuntimeException("Timed out waiting for browser to install")
        }
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to install browsers, exit code: ${process.exitValue()}")
        }
    }

    protected override fun driverDir() = driverTempDir
}
