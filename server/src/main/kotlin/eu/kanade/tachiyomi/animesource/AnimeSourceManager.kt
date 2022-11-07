package eu.kanade.tachiyomi.animesource

import android.content.Context
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import rx.Observable

open class AnimeSourceManager(private val context: Context) {

    private val sourcesMap = mutableMapOf<Long, AnimeSource>()

    private val stubSourcesMap = mutableMapOf<Long, StubSource>()

    init {
        createInternalSources().forEach { registerSource(it) }
    }

    open fun get(sourceKey: Long): AnimeSource? {
        return sourcesMap[sourceKey]
    }

    fun getOrStub(sourceKey: Long): AnimeSource {
        return sourcesMap[sourceKey] ?: stubSourcesMap.getOrPut(sourceKey) {
            StubSource(sourceKey)
        }
    }

    fun getOnlineSources() = sourcesMap.values.filterIsInstance<AnimeHttpSource>()

    fun getCatalogueSources() = sourcesMap.values.filterIsInstance<AnimeCatalogueSource>()

    internal fun registerSource(source: AnimeSource, overwrite: Boolean = false) {
        if (overwrite || !sourcesMap.containsKey(source.id)) {
            sourcesMap[source.id] = source
        }
        if (overwrite || !stubSourcesMap.containsKey(source.id)) {
            stubSourcesMap[source.id] = StubSource(source.id)
        }
    }

    internal fun unregisterSource(source: AnimeSource) {
        sourcesMap.remove(source.id)
    }

    private fun createInternalSources(): List<AnimeSource> = listOf(
//        LocalSource(context)
    )

    private inner class StubSource(override val id: Long) : AnimeSource {

        override val name: String
            get() = id.toString()

        override fun fetchAnimeDetails(anime: SAnime): Observable<SAnime> {
            return Observable.error(getSourceNotInstalledException())
        }

        override fun fetchEpisodeList(anime: SAnime): Observable<List<SEpisode>> {
            return Observable.error(getSourceNotInstalledException())
        }

        override fun fetchVideoList(episode: SEpisode): Observable<List<Video>> {
            return Observable.error(getSourceNotInstalledException())
        }

        override fun toString(): String {
            return name
        }

        private fun getSourceNotInstalledException(): Exception {
            return Exception("source not found")
        }
    }
}
