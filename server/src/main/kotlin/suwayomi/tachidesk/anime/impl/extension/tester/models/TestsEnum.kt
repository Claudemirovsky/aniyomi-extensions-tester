package suwayomi.tachidesk.anime.impl.extension.tester.models

enum class TestsEnum {
    POPULAR,
    LATEST,
    SEARCH,
    ANIDETAILS,
    EPLIST,
    VIDEOLIST,
    ;

    companion object {
        fun testList(): String {
            return entries.joinToString(",") { it.name.lowercase() }
        }
    }
}
