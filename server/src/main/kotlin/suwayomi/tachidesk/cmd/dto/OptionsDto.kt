package suwayomi.tachidesk.cmd.dto

data class OptionsDto(
    val apksPath: String,
    val configs: ConfigsDto,
    val debugMode: Boolean,
    val proxy: String?,
    val tmpDir: String,
    val userAgent: String?
)

data class ConfigsDto(
    val animeUrl: String = "",
    val checkThumbnails: Boolean = false,
    val dateFormat: String = "",
    val episodeUrl: String = "",
    val episodeNumber: Int = -1,
    val increment: Boolean = false,
    val printJson: Boolean = false,
    val resultsCount: Int = 2,
    val searchStr: String = "",
    val showAll: Boolean = false,
    val stopOnError: Boolean = false,
    val tests: String = ""
)
