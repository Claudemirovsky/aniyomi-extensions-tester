package eu.kanade.tachiyomi.animesource.model

import kotlinx.serialization.Serializable

@Serializable
data class SAnimeImpl(
    override var url: String = "",

    override var title: String = "",

    override var artist: String? = null,

    override var author: String? = null,

    override var description: String? = null,

    override var genre: String? = null,

    override var status: Int = 0,

    override var thumbnail_url: String? = null,

    override var initialized: Boolean = false
) : SAnime
