package eu.kanade.tachiyomi.animesource.model

import kotlinx.serialization.Serializable

data class AnimesPage(val animes: List<SAnime>, val hasNextPage: Boolean)

@Serializable
data class AnimesPageDto(
    val animes: List<SAnimeImpl>,
    val hasNextPage: Boolean
) {
    constructor(page: AnimesPage) : this(
        page.animes.map { it as SAnimeImpl },
        page.hasNextPage
    )
}
