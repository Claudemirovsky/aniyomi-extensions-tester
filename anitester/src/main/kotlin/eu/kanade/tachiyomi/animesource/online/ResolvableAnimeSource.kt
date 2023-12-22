package eu.kanade.tachiyomi.animesource.online

import eu.kanade.tachiyomi.animesource.AnimeSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode

/**
 * A source that may handle opening an SAnime or SEpisode for a given URI.
 *
 * @since extensions-lib 14
 */
interface ResolvableAnimeSource : AnimeSource {

    /**
     * Returns what the given URI may open.
     * Returns [UriType.Unknown] if the source is not able to resolve the URI.
     *
     * @since extensions-lib 14
     */
    fun getUriType(uri: String): UriType

    /**
     * Called if [getUriType] is [UriType.Anime].
     * Returns the corresponding SAnime, if possible.
     *
     * @since extensions-lib 14
     */
    suspend fun getAnime(uri: String): SAnime?

    /**
     * Called if [getUriType] is [UriType.Episode].
     * Returns the corresponding SEpisode, if possible.
     *
     * @since extensions-lib 14
     */
    suspend fun getEpisode(uri: String): SEpisode?
}

sealed interface UriType {
    data object Anime : UriType
    data object Episode : UriType
    data object Unknown : UriType
}
