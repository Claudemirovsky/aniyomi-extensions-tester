package suwayomi.tachidesk.anime.impl.extension.tester.models

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@ExperimentalSerializationApi
@Serializable
data class TestsResultsDto(
    var popular: ResultDto? = null,
    var latest: ResultDto? = null,
    var search: ResultDto? = null,
    var details: ResultDto? = null,
    var episodes: ResultDto? = null,
    var videos: ResultDto? = null
)

@ExperimentalSerializationApi
@Serializable
data class ResultDto(
    val result: JsonElement? = null,
    val error: String? = null,
    @EncodeDefault
    val passed: Boolean = true
)

@ExperimentalSerializationApi
@Serializable
data class SourceResultsDto(
    val name: String,
    val results: TestsResultsDto
)
