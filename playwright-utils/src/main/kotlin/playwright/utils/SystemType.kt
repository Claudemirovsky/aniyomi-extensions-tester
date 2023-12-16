package playwright.utils

enum class SystemType {
    /* GNU */ LINUX,
    MAC,
    WINDOWS,
    ;

    companion object {
        val currentSystem by lazy {
            val systemString = System.getProperty("os.name")
                .substringBefore(" ")
                .uppercase()
            runCatching { valueOf(systemString) }.getOrDefault(LINUX)
        }
    }
}
