package eu.kanade.tachiyomi

object BuildConfig {
    const val VERSION_NAME = suwayomi.server.BuildConfig.NAME
    val VERSION_CODE = suwayomi.server.BuildConfig.REVISION.trimStart('r').toInt()
}
