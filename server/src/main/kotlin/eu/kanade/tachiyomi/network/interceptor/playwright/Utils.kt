package eu.kanade.tachiyomi.network.interceptor.playwright

import java.io.File
import java.io.FilenameFilter

enum class SystemType {
    /* GNU */ LINUX,
    MAC,
    WINDOWS;

    companion object {
        val currentSystem by lazy {
            val systemString = System.getProperty("os.name")
                .substringBefore(" ")
                .uppercase()
            runCatching { valueOf(systemString) }.getOrDefault(LINUX)
        }
    }
}

fun findBinary(vararg binaries: String): String? {
    val pathSeparator = System.getProperty("path.separator")
    val envPath = System.getenv("PATH") ?: System.getenv("PATHEXT")!!

    val binaryFilter = object : FilenameFilter {
        override fun accept(dir: File, name: String): Boolean =
            binaries.any(name::equals)
    }

    envPath.split(pathSeparator).forEach {
        val results = File(it).list(binaryFilter)
        if (results != null && results.isNotEmpty()) {
            return "$it/${results.first()}"
        }
    }

    return null
}

fun getCacheDir(): File {
    // If nothing works, then explode in flames (NPE)
    val home = System.getProperty("user.home") ?: System.getProperty("os.home")!!

    // Based on playwright-core/src/server/registry/index.ts
    val cachePath = when (SystemType.currentSystem) {
        SystemType.LINUX ->
            System.getenv("XDG_CACHE_HOME")
                ?.let(::File)
                ?: File(home, ".cache")
        SystemType.WINDOWS ->
            System.getenv("LOCALAPPDATA")
                ?.let(::File)
                ?: File(File(home, "AppData"), "Local")
        SystemType.MAC -> File(File(home, "Library"), "Caches")
    }
    return File(cachePath, "ms-playwright")
}
