package suwayomi.tachidesk.anime.impl.extension.tester

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SAnimeImpl
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SEpisodeImpl
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.model.VideoDto
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import rx.Observable
import suwayomi.tachidesk.cmd.RED
import suwayomi.tachidesk.cmd.RESET
import suwayomi.tachidesk.cmd.dto.ConfigsDto
import suwayomi.tachidesk.cmd.printAnime
import suwayomi.tachidesk.cmd.printEpisode
import suwayomi.tachidesk.cmd.printLine
import suwayomi.tachidesk.cmd.printTitle
import suwayomi.tachidesk.cmd.printVideo
import kotlin.system.exitProcess

class ExtensionTests(
    private val source: AnimeHttpSource,
    private val configs: ConfigsDto
) {
    private val logger = KotlinLogging.logger {}

    private val json = Json { prettyPrint = true }

    private var ANIDETAILS_URL: String = configs.animeUrl
    private var ANIME_OBJ: SAnime? = null

    private var EP_URL: String = configs.episodeUrl
    private var EP_OBJ: SEpisode? = null

    private val tests by lazy {
        val list = configs.tests.split(",")
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
                    TestsEnum.POPULAR -> testPopularAnimesPage()
                    TestsEnum.LATEST -> {
                        if (source.supportsLatest) testLatestAnimesPage()
                    }
                    TestsEnum.SEARCH -> testSearchAnimesPage()
                    TestsEnum.ANIDETAILS -> testAnimeDetails()
                    TestsEnum.EPLIST -> testEpisodeList()
                    TestsEnum.VIDEOLIST -> testVideoList()
                    else -> null
                }
            } catch (e: Exception) {
                logger.error("Test($it): ", e)
                if (configs.stopOnError)
                    exitProcess(-1)
            }
        }
        println()
        printTitle("END ALL TESTS")
    }

    private fun testPopularAnimesPage() {
        printAnimesPage("POPULAR ANIMES PAGE") { page: Int ->
            source.fetchPopularAnime(page)
        }
    }

    private fun testLatestAnimesPage() {
        printAnimesPage("LATEST ANIMES PAGE") { page: Int ->
            source.fetchLatestUpdates(page)
        }
    }

    private fun testSearchAnimesPage() {
        printAnimesPage("SEARCH ANIMES PAGE") { page: Int ->
            source.fetchSearchAnime(page, configs.searchStr, AnimeFilterList())
        }
    }

    private fun testAnimeDetails() {
        println()
        printTitle("START ANIME DETAILS TEST")

        val anime = getSAnime()

        val details = parseObservable<SAnime>(
            source.fetchAnimeDetails(anime)
        )

        details.url.ifEmpty { details.url = anime.url }
        printItemOrJson<SAnime>(details)
        printTitle("END ANIME DETAILS TEST")
    }

    private fun testEpisodeList() {
        println()
        val title = "EPISODE LIST"
        printTitle("START $title TEST")

        val anime = getSAnime()

        val result = parseObservable<List<SEpisode>>(
            source.fetchEpisodeList(anime)
        )

        printLine("Episodes", result.size.toString())

        if (result.size > 0) {
            if (configs.episodeUrl.isBlank()) {
                EP_URL = result.first().url
            } else if (configs.episodeNumber > -1) {
                run loop@{
                    result.forEach {
                        if (it.episode_number.toInt() == configs.episodeNumber) {
                            EP_URL = it.url
                            return@loop
                        }
                    }
                }
            } else EP_OBJ = result.first()

            val episodeList = if (!configs.showAll) {
                result.take(configs.resultsCount)
            } else result

            episodeList.forEach {
                printItemOrJson<SEpisode>(it)
            }
        }

        printTitle("END $title TEST")
    }

    private fun testVideoList() {
        val title = "VIDEO LIST"
        println()
        printTitle("START $title TEST")

        val episode = EP_OBJ ?: SEpisode.create().apply {
            url = EP_URL
        }

        printLine("EP URL", episode.url)

        val videoList = parseObservable<List<Video>>(
            source.fetchVideoList(episode)
        )

        printLine("Videos", videoList.size.toString())

        videoList.forEach {
            printItemOrJson<Video>(it)
        }

        printTitle("END $title TEST")
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun <T> printItemOrJson(item: T) {
        if (configs.printJson) {
            val jsonStr = when (item) {
                is SAnime -> json.encodeToString(item as SAnimeImpl)
                is SEpisode -> json.encodeToString(item as SEpisodeImpl)
                is Video -> json.encodeToString(VideoDto(item))
                else -> null
            }
            jsonStr?.let(::println)
        } else when (item) {
            is SAnime -> printAnime(item)
            is SEpisode -> printEpisode(item)
            is Video -> printVideo(item)
            else -> null
        }
    }

    private fun getSAnime(): SAnime {
        return ANIME_OBJ ?: SAnime.create().apply {
            url = ANIDETAILS_URL
        }
    }

    private fun printAnimesPage(title: String, block: (page: Int) -> Observable<AnimesPage>) {
        println()
        printTitle("START $title TEST")
        var page = 0
        while (true) {
            page++
            val results = parseObservable<AnimesPage>(
                block(page)
            )
            println()
            printLine("Page", "$page")
            printLine("Results", results.animes.size.toString())
            printLine("Has next page", results.hasNextPage.toString())
            val animes = results.animes.let {
                if (!configs.showAll)
                    it.take(configs.resultsCount)
                else it
            }

            animes.forEach {
                if (ANIDETAILS_URL.isBlank() && ANIME_OBJ == null)
                    ANIME_OBJ = it

                printItemOrJson<SAnime>(it)
            }

            if (!configs.increment || !results.hasNextPage || page >= 2) break
            else println("${RED}Incrementing page number$RESET")
        }
        printTitle("END $title TEST")
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
