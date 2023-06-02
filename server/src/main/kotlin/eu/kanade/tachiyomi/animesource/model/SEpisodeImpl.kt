package eu.kanade.tachiyomi.animesource.model

import kotlinx.serialization.Serializable

@Serializable
data class SEpisodeImpl(
    override var url: String = "",

    override var name: String = "",

    override var date_upload: Long = 0,

    override var episode_number: Float = -1f,

    override var scanlator: String? = null,
) : SEpisode
