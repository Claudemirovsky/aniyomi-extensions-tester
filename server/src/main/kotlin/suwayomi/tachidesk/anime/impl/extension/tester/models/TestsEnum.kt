package suwayomi.tachidesk.anime.impl.extension.tester.models

enum class TestsEnum {
    POPULAR,
    LATEST,
    SEARCH,
    ANIDETAILS,
    EPLIST,
    VIDEOLIST;

    companion object {
        fun getValues(): String {
            return values().joinToString(",") { it.name.lowercase() }
        }
    }
}
