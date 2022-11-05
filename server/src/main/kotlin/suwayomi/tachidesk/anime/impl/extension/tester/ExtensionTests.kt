package suwayomi.tachidesk.anime.impl.extension.tester

import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnimeImpl
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import rx.Observable
import suwayomi.tachidesk.cmd.dto.ConfigsDto
import kotlin.system.exitProcess

class ExtensionTests(
    private val source: AnimeHttpSource,
    private val config: ConfigsDto
) {
    private val logger = KotlinLogging.logger {}

    private val json = Json { prettyPrint = true }

    private var ANIDETAILS_URL: String = config.animeUrl
    private var EP_URL: String = config.animeUrl

    private val tests by lazy {
        val list = config.tests.split(",")
        list.mapNotNull {
            runCatching {
                TestsEnum.valueOf(it.uppercase())
            }.getOrNull()
        }
    }

    fun runTests() {
        tests.forEach {
            try {
                when (it) {
                    TestsEnum.POPULAR -> testPopularAnimesPage()/*
                    TestsEnum.LATEST -> testLatestAnimesPage()
                    TestsEnum.SEARCH -> testSearchAnimesPage()
                    TestsEnun.ANIDETAILS -> testAnimeDetails()
                    TestsEnum.EPLIST -> testEpisodeList()
                    TestsEnum.VIDEOLIST -> testVideoList()*/
                    else -> null
                }
            } catch (e: Exception) {
                if (config.stopOnError) {
                    logger.error("Test($it): ", e)
                    exitProcess(-1)
                }
            }
        }
    }

    private fun testPopularAnimesPage() {
        val popularAnimes = parseObservable<AnimesPage>(source.fetchPopularAnime(1))
        val animes = popularAnimes.animes.map {
            val anime = it as SAnimeImpl
            json.encodeToString(anime)
        }
        println(animes)
    }

    private fun <T> parseObservable(observable: Observable<T>): T {
        var data: T? = null
        observable.subscribe(
            { it -> data = it },
            { e: Throwable -> throw e }
        )
        return data!!
    }
}
