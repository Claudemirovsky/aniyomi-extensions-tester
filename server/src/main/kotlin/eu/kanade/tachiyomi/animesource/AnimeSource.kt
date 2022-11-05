package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import rx.Observable
import uy.kohesive.injekt.api.get

/**
 * A basic interface for creating a source. It could be an online source, a local source, etc...
 */
interface AnimeSource {

    /**
     * Id for the source. Must be unique.
     */
    val id: Long

    /**
     * Name of the source.
     */
    val name: String

    val lang: String
        get() = ""

    /**
     * Returns an observable with the updated details for a anime.
     *
     * @param anime the anime to update.
     */
    fun fetchAnimeDetails(anime: SAnime): Observable<SAnime> =
        throw IllegalStateException("Not used")

    /**
     * Returns an observable with all the available episodes for an anime.
     *
     * @param anime the anime to update.
     */
    fun fetchEpisodeList(anime: SAnime): Observable<List<SEpisode>> =
        throw IllegalStateException("Not used")

    /**
     * Returns an observable with a list of video for the episode of an anime.
     *
     * @param episode the episode to get the link for.
     */
    fun fetchVideoList(episode: SEpisode): Observable<List<Video>> =
        Observable.empty()
}
