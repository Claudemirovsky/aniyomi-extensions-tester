package eu.kanade.tachiyomi

object BuildConfig {
    const val VERSION_NAME = anitester.BuildConfig.NAME
    val VERSION_CODE = anitester.BuildConfig.REVISION.trimStart('r').toInt()
}
