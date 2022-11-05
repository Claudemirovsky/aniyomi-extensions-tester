package eu.kanade.tachiyomi.animesource

import android.content.Context
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import rx.Observable

class LocalAnimeSource(private val context: Context) : AnimeCatalogueSource {
    companion object {
        const val ID = 0L
    }

    override val id = ID
    override val name = "Local source"
    override val lang = ""
    override val supportsLatest = true

    override fun fetchAnimeDetails(anime: SAnime): Observable<SAnime> {
        TODO("Not yet implemented")
    }

    override fun fetchEpisodeList(anime: SAnime): Observable<List<SEpisode>> {
        TODO("Not yet implemented")
    }

    override fun fetchVideoList(episode: SEpisode): Observable<List<Video>> {
        TODO("Not yet implemented")
    }
    override fun fetchPopularAnime(page: Int): Observable<AnimesPage> {
        TODO("Not yet implemented")
    }

    override fun fetchSearchAnime(page: Int, query: String, filters: AnimeFilterList): Observable<AnimesPage> {
        TODO("Not yet implemented")
    }

    override fun fetchLatestUpdates(page: Int): Observable<AnimesPage> {
        TODO("Not yet implemented")
    }

    override fun getFilterList(): AnimeFilterList {
        TODO("Not yet implemented")
    }
}
