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
import suwayomi.tachidesk.cmd.timeTestFromEnum
import java.text.SimpleDateFormat
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

class FailedTestException(error: String = "") : Exception(error)

class ExtensionTests(
    private val source: AnimeHttpSource,
    private val configs: ConfigsDto
) {
    private val logger = KotlinLogging.logger {}

    private val json = Json { prettyPrint = true }

    private val DATE_FORMATTER by lazy {
        runCatching {
            SimpleDateFormat(configs.dateFormat)
        }.getOrDefault(SimpleDateFormat("dd/mm/yyyy"))
    }

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

    @ExperimentalTime
    fun runTests() {
        tests.forEach { test ->
            try {
                val testFunction: () -> Unit = when (test) {
                    TestsEnum.POPULAR -> { { testPopularAnimesPage() } }
                    TestsEnum.LATEST -> { { testLatestAnimesPage() } }
                    TestsEnum.SEARCH -> { { testSearchAnimesPage() } }
                    TestsEnum.ANIDETAILS -> { { testAnimeDetails() } }
                    TestsEnum.EPLIST -> { { testEpisodeList() } }
                    TestsEnum.VIDEOLIST -> { { testVideoList() } }
                }

                if (test == TestsEnum.LATEST) {
                    if (source.supportsLatest)
                        timeTestFromEnum(test) { testFunction() }
                } else timeTestFromEnum(test) { testFunction() }
            } catch (e: FailedTestException) {
                printTitle("${test.name} TEST FAILED", barColor = RED)
                if (configs.stopOnError)
                    exitProcess(-1)
            } catch (e: Exception) {
                logger.error("Test($test): ", e)
                if (configs.stopOnError)
                    exitProcess(-1)
            }
        }
    }

    private fun testPopularAnimesPage() {
        printAnimesPage() { page: Int ->
            source.fetchPopularAnime(page)
        }
    }

    private fun testLatestAnimesPage() {
        printAnimesPage() { page: Int ->
            source.fetchLatestUpdates(page)
        }
    }

    private fun testSearchAnimesPage() {
        printAnimesPage() { page: Int ->
            source.fetchSearchAnime(page, configs.searchStr, AnimeFilterList())
        }
    }

    private fun testAnimeDetails() {
        val anime = getSAnime()

        val details = parseObservable<SAnime>(
            source.fetchAnimeDetails(anime)
        )

        details.url.ifEmpty { details.url = anime.url }
        printItemOrJson<SAnime>(details)
    }

    private fun testEpisodeList() {
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
    }

    private fun testVideoList() {
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
            is SEpisode -> printEpisode(item, DATE_FORMATTER)
            is Video -> printVideo(item)
            else -> null
        }
    }

    private fun getSAnime(): SAnime {
        return ANIME_OBJ ?: SAnime.create().apply {
            url = ANIDETAILS_URL
        }
    }

    private fun printAnimesPage(block: (page: Int) -> Observable<AnimesPage>) {
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
    }

    private fun <T> parseObservable(observable: Observable<T>): T {
        var data: T? = null
        observable
            .subscribe(
                { it -> data = it },
                { e: Throwable -> logger.error("ERROR: ", e) }
            )
        return data ?: throw FailedTestException()
    }
}
