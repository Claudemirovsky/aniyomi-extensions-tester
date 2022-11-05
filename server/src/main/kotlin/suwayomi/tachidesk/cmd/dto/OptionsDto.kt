package suwayomi.tachidesk.cmd.dto

data class OptionsDto(
    val apksPath: String,
    val tmpDir: String,
    val debugMode: Boolean,
    val configs: ConfigsDto
)

data class ConfigsDto(
    val animeUrl: String = "",
    val episodeUrl: String = "",
    val episodeNumber: Int = 1,
    val printJson: Boolean = false,
    val showAll: Boolean = false,
    val stopOnError: Boolean = false,
    val tests: String = ""
)
