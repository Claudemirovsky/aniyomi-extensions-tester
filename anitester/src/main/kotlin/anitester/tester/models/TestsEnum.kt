package anitester.tester.models

enum class TestsEnum {
    POPULAR,
    LATEST,
    SEARCH,
    ANIDETAILS,
    EPLIST,
    VIDEOLIST,
    ;

    val title: String by lazy {
        when (this) {
            ANIDETAILS -> "ANIME DETAILS"
            EPLIST -> "EPISODE LIST"
            VIDEOLIST -> "VIDEO LIST"
            else -> "$name PAGE"
        } + " TEST"
    }

    companion object {
        fun testList(): String {
            return entries.joinToString(",") { it.name.lowercase() }
        }
    }
}
