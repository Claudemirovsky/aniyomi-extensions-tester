package suwayomi.tachidesk.anime.impl.extension.tester

import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.AnimesPageDto
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SAnimeImpl
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SEpisodeImpl
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.model.VideoDto
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.HEAD
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import okhttp3.Headers
import rx.Observable
import suwayomi.tachidesk.anime.impl.extension.tester.models.ResultDto
import suwayomi.tachidesk.anime.impl.extension.tester.models.TestsEnum
import suwayomi.tachidesk.anime.impl.extension.tester.models.TestsResultsDto
import suwayomi.tachidesk.cmd.RED
import suwayomi.tachidesk.cmd.RESET
import suwayomi.tachidesk.cmd.dto.ConfigsDto
import suwayomi.tachidesk.cmd.printAnime
import suwayomi.tachidesk.cmd.printEpisode
import suwayomi.tachidesk.cmd.printLine
import suwayomi.tachidesk.cmd.printTitle
import suwayomi.tachidesk.cmd.printVideo
import suwayomi.tachidesk.cmd.timeTestFromEnum
import java.net.ProtocolException
import java.text.SimpleDateFormat
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

class FailedTestException(error: Throwable) : Exception(error) {
    constructor(error: String = "") : this(Exception(error))
}

@ExperimentalSerializationApi
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

    private val TESTS_RESULTS_DTO = TestsResultsDto()

    // Comma-separated list of tests to be done
    private val tests by lazy {
        val list = configs.tests.split(",")
        list.mapNotNull {
            runCatching {
                TestsEnum.valueOf(it.uppercase())
            }.getOrNull()
        }
    }

    @ExperimentalTime
    fun runTests(): TestsResultsDto {
        tests.forEach { test ->
            try {
                // Returns the related function to each test inside a block
                val testFunction: () -> Unit = when (test) {
                    TestsEnum.POPULAR -> { { testPopularAnimesPage() } }
                    TestsEnum.LATEST -> { { testLatestAnimesPage() } }
                    TestsEnum.SEARCH -> { { testSearchAnimesPage() } }
                    TestsEnum.ANIDETAILS -> { { testAnimeDetails() } }
                    TestsEnum.EPLIST -> { { testEpisodeList() } }
                    TestsEnum.VIDEOLIST -> { { testVideoList() } }
                }

                // Prevents running LATEST test if the source doesnt support it.
                if (test == TestsEnum.LATEST) {
                    if (source.supportsLatest)
                        timeTestFromEnum(test) { testFunction() }
                } else timeTestFromEnum(test) { testFunction() }
            } catch (e: FailedTestException) {
                writeTestError(test, e)
                printTitle("${test.name} TEST FAILED", barColor = RED)
                if (configs.stopOnError)
                    exitProcess(-1)
            } catch (e: Throwable) {
                writeTestError(test, e)
                logger.error("Test($test): ", e)
                if (configs.stopOnError)
                    exitProcess(-1)
            }
        }
        return TESTS_RESULTS_DTO
    }

    private fun testPopularAnimesPage() {
        printAnimesPage(TestsEnum.POPULAR) { page: Int ->
            source.fetchPopularAnime(page)
        }
    }

    private fun testLatestAnimesPage() {
        printAnimesPage(TestsEnum.LATEST) { page: Int ->
            source.fetchLatestUpdates(page)
        }
    }

    private fun testSearchAnimesPage() {
        printAnimesPage(TestsEnum.SEARCH) { page: Int ->
            source.fetchSearchAnime(page, configs.searchStr, source.getFilterList())
        }
    }

    private fun testAnimeDetails() {
        val anime = getSAnime()

        val details = parseObservable<SAnime>(
            source.fetchAnimeDetails(anime)
        )

        details.url.ifEmpty { details.url = anime.url }
        if (configs.checkThumbnails)
            details.is_thumbnail_loading = testMediaResult(details.thumbnail_url)
        printItemOrJson<SAnime>(details)
        writeTestSuccess(TestsEnum.ANIDETAILS) {
            val animeObj = details as SAnimeImpl
            json.encodeToJsonElement(animeObj).jsonObject
        }
    }

    private fun testEpisodeList() {
        val anime = getSAnime()

        val result = parseObservable<List<SEpisode>>(
            source.fetchEpisodeList(anime)
        )

        printLine("Episodes", result.size.toString())

        if (result.size > 0) {
            // Sets the episode url to use in videoList test.
            if (configs.episodeUrl.isNotBlank()) {
                EP_URL = configs.episodeUrl
            } else if (configs.episodeNumber > -1) {
                runCatching {
                    EP_OBJ = result.first {
                        it.episode_number.toInt() == configs.episodeNumber
                    }
                }
            } else EP_OBJ = result.first()

            // Cut the results list if `configs.showAll` isnt enabled.
            val episodeList = if (!configs.showAll) {
                result.take(configs.resultsCount)
            } else result

            episodeList.forEach {
                printItemOrJson<SEpisode>(it)
            }
            writeTestSuccess(TestsEnum.EPLIST) {
                val episodeOBJList = episodeList.map { it as SEpisodeImpl }
                json.encodeToJsonElement(episodeOBJList).jsonArray
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
        if (videoList.size == 0)
            throw FailedTestException("Empty video list")

        videoList.forEach {
            // Tests if the video is loading
            // It runs everytime, but its really fast and does not use much bandwith
            // .... Unless something went wrong
            val test = runCatching {
                testMediaResult(it.videoUrl ?: it.url, true, it.headers)
            }.getOrDefault(false)

            it.isWorking = test
            printItemOrJson<Video>(it)
        }

        writeTestSuccess(TestsEnum.VIDEOLIST) {
            val videoDtoList = videoList.map { VideoDto(it) }
            json.encodeToJsonElement(videoDtoList).jsonArray
        }
    }

    /**
     * Pretty-prints a SAnime/SEpisode/Video object OR dump a json of them, depending of `configs.printJson` option
     *
     * @param item The object to be printed as colored text or json
     */
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
            is SAnime -> printAnime(item, configs.checkThumbnails)
            is SEpisode -> printEpisode(item, DATE_FORMATTER)
            is Video -> printVideo(item)
        }
    }

    /**
     * Tests if a video is playing or if a thumbnail image is loading, based on the response headers.
     *
     * @param url The video/thumbnail url.
     * @param isVideo An switch to control the type of mimetypes to be used to check the response.
     * @param headers Video headers that may be needed to complete the request.
     * @return The result of the test: It loads or not?
     */
    private fun testMediaResult(
        url: String?,
        isVideo: Boolean = false,
        headers: Headers? = null,
        supportsHEAD: Boolean = true
    ): Boolean {
        if (url == null) return false
        // Prevents loading a heavy image or video by requesting only ONE byte.
        val newHeaders = Headers.Builder().apply {
            addAll(headers ?: source.headers)
            add("Range", "bytes=0-1")
        }.build()

        // We use the HEAD request type because we just want the response headers.
        //
        // If the source does not support it (= returns UNDEFINED aa content-type),
        // We use GET instead, at the risk of maybe downloading a entire episode
        // Or in the best-worst case just downloading a m3u8 playlist.
        val req = try {
            val request = if (supportsHEAD) HEAD(url, newHeaders) else GET(url, newHeaders)
            source.client.newCall(request).execute()
        } catch (e: ProtocolException) {
            // Sometimes OkHttp just forgets that it supports HTTP 206 partial content.
            // So trying again will not hurt.
            return testMediaResult(url, isVideo, headers, supportsHEAD)
        }

        // HTTP codes outside 2xx or 3xx are a bad signal...
        if (!req.isSuccessful) return false

        val resType = req.header("content-type", "") ?: ""
        if (resType == "undefined") {
            if (!supportsHEAD) return false
            else return testMediaResult(url, isVideo, headers, false)
        }

        val mimeTypes = listOf("video/", "/x-mpegURL", "/vnd.apple.mpegurl")
        return if (isVideo) mimeTypes.any { it in resType } else "image/" in resType
    }

    private fun getSAnime(): SAnime {
        return ANIME_OBJ ?: SAnime.create().apply {
            url = ANIDETAILS_URL
        }
    }

    /**
     * Prints the anime-page that a lambda block will return.
     *
     * @param block The lambda that receives a page number and returns a `AnimesPage` object.
     */
    private fun printAnimesPage(
        test: TestsEnum,
        block: (page: Int) -> Observable<AnimesPage>
    ) {
        var page = 0
        val animesPages = mutableListOf<AnimesPageDto>()
        while (true) {
            page++
            val results = parseObservable<AnimesPage>(
                block(page)
            )
            if (configs.completeResults)
                animesPages.add(AnimesPageDto(results))
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
                // Sets the ANIME_OBJ for the anidetails test if needed.
                if (ANIDETAILS_URL.isBlank() && ANIME_OBJ == null)
                    ANIME_OBJ = it
                if (configs.checkThumbnails)
                    it.is_thumbnail_loading = testMediaResult(it.thumbnail_url)
                printItemOrJson<SAnime>(it)
            }

            if (!configs.increment || !results.hasNextPage || page >= 2) break
            else println("${RED}Incrementing page number$RESET")
        }
        writeTestSuccess(test) {
            json.encodeToJsonElement(animesPages).jsonArray
        }
    }

    /*
     * Returns the value of a observable object, or logs and throws a error.
     *
     * @param observable The `Observable<T>` object to be used.
     * @return The value returned by the `Observable` object
     */
    private fun <T> parseObservable(observable: Observable<T>): T {
        var data: T? = null
        var error = Throwable()
        observable
            .subscribe(
                { it -> data = it },
                { e: Throwable ->
                    error = e
                    logger.error("ERROR: ", e)
                }
            )
        return data ?: throw FailedTestException(error)
    }

    /**
     * Adds an "failed" result to `TESTS_RESULTS_DTO`.
     *
     * @param test The test that failed.
     * @param e The exception returned by the failed test.
     */
    private fun writeTestError(test: TestsEnum, e: Throwable) {
        val result = ResultDto(null, e.toString(), false)
        setTestResult(test, result)
    }

    /**
     * Adds an "passed" result to `TESTS_RESULTS_DTO`.
     *
     * @param test The test that ran successfully.
     * @param resultFetcher An lambda block that will return the data generated by the test. It wil run ONLY if `configs.completeResults` is set (--complete-results -C).
     */
    private fun writeTestSuccess(test: TestsEnum, resultFetcher: () -> JsonElement) {
        val res = if (!configs.completeResults) null else resultFetcher()
        val result = ResultDto(res)
        setTestResult(test, result)
    }

    /**
     * Sets the value of a property from `TESTS_RESULT_DTO`, based on the test name.
     *
     * @param test The test name / type.
     * @param result The result from the test.
     */
    private fun setTestResult(test: TestsEnum, result: ResultDto) {
        when (test) {
            TestsEnum.POPULAR -> {
                TESTS_RESULTS_DTO.popular = result
            }
            TestsEnum.LATEST -> {
                TESTS_RESULTS_DTO.latest = result
            }
            TestsEnum.SEARCH -> {
                TESTS_RESULTS_DTO.search = result
            }
            TestsEnum.ANIDETAILS -> {
                TESTS_RESULTS_DTO.details = result
            }
            TestsEnum.EPLIST -> {
                TESTS_RESULTS_DTO.episodes = result
            }
            TestsEnum.VIDEOLIST -> {
                TESTS_RESULTS_DTO.videos = result
            }
        }
    }
}
