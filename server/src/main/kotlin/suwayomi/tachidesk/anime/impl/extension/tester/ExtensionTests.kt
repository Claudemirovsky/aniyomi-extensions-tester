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
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.Headers
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
import suwayomi.tachidesk.cmd.timeTest
import java.net.ProtocolException
import java.text.SimpleDateFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

class FailedTestException(error: Throwable) : Exception(error) {
    constructor(error: String = "") : this(Exception(error))
}

@ExperimentalSerializationApi
class ExtensionTests(
    private val source: AnimeHttpSource,
    private val configs: ConfigsDto,
    private val shouldDumpJson: Boolean,
) {
    private val logger = KotlinLogging.logger {}

    private val json = Json { prettyPrint = true }

    private val DATE_FORMATTER by lazy {
        runCatching {
            SimpleDateFormat(configs.dateFormat)
        }.getOrElse { SimpleDateFormat("dd/mm/yyyy") }
    }

    private var ANIDETAILS_URL: String = configs.animeUrl
    private var ANIME_OBJ: SAnime? = null

    private var EP_URL: String = configs.episodeUrl
    private var EP_OBJ: SEpisode? = null

    private val TESTS_RESULTS_DTO by lazy { TestsResultsDto() }

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
    @DelicateCoroutinesApi
    suspend fun runTests(): TestsResultsDto {
        tests.forEach { test ->
            // Prevents running LATEST test if the source doesnt support it.
            if (test == TestsEnum.LATEST && !source.supportsLatest) {
                return@forEach
            }

            try {
                val testFunction = when (test) {
                    TestsEnum.POPULAR -> ::testPopularAnimesPage
                    TestsEnum.LATEST -> ::testLatestAnimesPage
                    TestsEnum.SEARCH -> ::testSearchAnimesPage
                    TestsEnum.ANIDETAILS -> ::testAnimeDetails
                    TestsEnum.EPLIST -> ::testEpisodeList
                    TestsEnum.VIDEOLIST -> ::testVideoList
                }

                val latch = CountDownLatch(1)
                var exception: Throwable? = null

                val coro = GlobalScope.launch(Dispatchers.Default) {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            timeTest(test.title) { testFunction() }
                        }.onFailure { exception = it }
                        latch.countDown()
                    }
                }

                latch.await(configs.timeoutSeconds, TimeUnit.SECONDS).also { isOk ->
                    coro.cancel()
                    exception?.let { throw it }
                    if (!isOk) throw FailedTestException("Timeout!")
                }
            } catch (e: FailedTestException) {
                treatTestException(test, e)
            } catch (e: Throwable) {
                treatTestException(test, e, showStackTrace = true)
            }
        }
        return TESTS_RESULTS_DTO
    }

    private fun treatTestException(test: TestsEnum, e: Throwable, showStackTrace: Boolean = false) {
        writeTestError(test, e)
        when {
            showStackTrace -> logger.error(e) { "Test($test): " }
            else -> logger.error { "Test($test): $e" }
        }
        printTitle("${test.title} FAILED", barColor = RED)
        if (configs.stopOnError) {
            exitProcess(-1)
        }
    }

    private suspend fun testPopularAnimesPage() {
        printAnimesPage(TestsEnum.POPULAR, source::getPopularAnime)
    }

    private suspend fun testLatestAnimesPage() {
        printAnimesPage(TestsEnum.LATEST, source::getLatestUpdates)
    }

    private suspend fun testSearchAnimesPage() {
        printAnimesPage(TestsEnum.SEARCH) { page: Int ->
            source.getSearchAnime(page, configs.searchStr, source.getFilterList())
        }
    }

    private suspend fun testAnimeDetails() {
        val anime = getSAnime()

        val details = source.getAnimeDetails(anime)

        details.url.ifEmpty { details.url = anime.url }
        if (configs.checkThumbnails) {
            details.is_thumbnail_loading = testMediaResult(details.thumbnail_url)
        }
        details.status_name = SAnime.getStatus(details.status)
        printItemOrJson(details)
        writeTestSuccess(TestsEnum.ANIDETAILS) {
            (details as SAnimeImpl)
                .let(json::encodeToJsonElement)
                .jsonObject
        }
    }

    private suspend fun testEpisodeList() {
        val anime = getSAnime()

        val result = source.getEpisodeList(anime)

        printLine("Episodes", result.size.toString())

        if (result.isEmpty()) {
            throw FailedTestException("Empty episode list")
        }

        // Sets the episode url to use in videoList test.
        if (configs.episodeUrl.isNotBlank()) {
            EP_URL = configs.episodeUrl
        } else if (configs.episodeNumber > -1) {
            runCatching {
                EP_OBJ = result.first {
                    it.episode_number.toInt() == configs.episodeNumber
                }
            }
        } else {
            EP_OBJ = result.first()
        }

        // Cut the results list if `configs.showAll` isnt enabled.
        val episodeList = if (!configs.showAll) {
            result.take(configs.resultsCount)
        } else {
            result
        }

        episodeList.forEach(::printItemOrJson)

        writeTestSuccess(TestsEnum.EPLIST) {
            episodeList.map { it as SEpisodeImpl }
                .let(json::encodeToJsonElement)
                .jsonArray
        }
    }

    private suspend fun testVideoList() {
        val episode = EP_OBJ ?: SEpisode.create().apply {
            url = EP_URL
        }

        printLine("EP URL", episode.url)

        val videoList = source.getVideoList(episode)

        printLine("Videos", videoList.size.toString())
        if (videoList.isEmpty()) {
            throw FailedTestException("Empty video list")
        }

        videoList.let {
            if (configs.checkVideos) {
                it.parallelMap { video ->
                    // Tests if the video is loading
                    video.isWorking = runCatching {
                        testMediaResult(video.videoUrl ?: video.url, true, video.headers)
                    }.getOrDefault(false)
                    video
                }
            } else { it }
        }.forEach(::printItemOrJson)

        writeTestSuccess(TestsEnum.VIDEOLIST) {
            videoList.map(::VideoDto)
                .let(json::encodeToJsonElement)
                .jsonArray
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
        } else {
            when (item) {
                is SAnime -> printAnime(item, configs.checkThumbnails)
                is SEpisode -> printEpisode(item, DATE_FORMATTER)
                is Video -> printVideo(item, configs.checkVideos)
            }
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
        supportsHEAD: Boolean = true,
    ): Boolean {
        if (url == null) return false
        // Prevents loading a heavy image or video by requesting only ONE byte.
        val newHeaders = Headers.Builder().apply {
            addAll(headers ?: source.headers)
            add("Range", "bytes=0-1")
        }.build()

        // We use the HEAD request type because we just want the response headers.
        //
        // If the source does not support it (= returns UNDEFINED as content-type),
        // We use GET instead, at the risk of maybe downloading a entire episode
        // Or in the best-worst case just downloading a m3u8 playlist.
        val req = try {
            val request = if (supportsHEAD) HEAD(url, newHeaders) else GET(url, newHeaders)
            source.client.newCall(request).execute().also { it.close() }
        } catch (e: ProtocolException) {
            // Sometimes OkHttp just forgets that it supports HTTP 206 partial content.
            // So trying again will not hurt.
            return testMediaResult(url, isVideo, headers, supportsHEAD)
        }

        // HTTP codes outside 2xx or 3xx are a bad signal...
        if (!req.isSuccessful) return false

        val resType = req.header("content-type", "") ?: ""
        if (resType == "undefined" || resType.isBlank()) {
            if (!supportsHEAD) {
                return false
            } else {
                return testMediaResult(url, isVideo, headers, false)
            }
        }

        return when {
            isVideo -> {
                VIDEO_MIMETYPES.any { it in resType } ||
                    (PLAYLIST_SUFFIXES.any { it in url } && "text/plain" in resType)
            }
            else -> "image/" in resType
        }
    }

    private val VIDEO_MIMETYPES = setOf(
        "video/",
        "application/mp4",
        "application/mpeg4",
        "/x-mpegURL",
        "application/vnd.apple.mpegurl",
        "application/octet-stream",
        "application/dash+xml",
    )

    private val PLAYLIST_SUFFIXES = setOf(".m3u8", ".mpd", ".dash")

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
    private suspend fun printAnimesPage(
        test: TestsEnum,
        block: suspend (page: Int) -> AnimesPage,
    ) {
        var page = 0
        val animesPages = mutableListOf<AnimesPageDto>()
        while (true) {
            page++
            val results = block(page)
            if (configs.completeResults) {
                animesPages.add(AnimesPageDto(results))
            }
            println()
            printLine("Page", "$page")
            printLine("Results", "${results.animes.size}")
            printLine("Has next page", "${results.hasNextPage}")
            val animes = results.animes.let {
                if (!configs.showAll) {
                    it.take(configs.resultsCount)
                } else {
                    it
                }
            }
            // Sets the ANIME_OBJ for the anidetails test if needed.
            if (ANIDETAILS_URL.isBlank() && ANIME_OBJ == null) {
                ANIME_OBJ = animes.first()
            }

            animes.let {
                if (configs.checkThumbnails) {
                    it.parallelMap { anime ->
                        val url = anime.thumbnail_url
                        anime.is_thumbnail_loading = testMediaResult(url)
                        anime
                    }
                } else { it }
            }.forEach(::printItemOrJson)

            if (!configs.increment || !results.hasNextPage || page >= 2) {
                break
            } else {
                println("${RED}Incrementing page number$RESET")
            }
        }
        writeTestSuccess(test) {
            json.encodeToJsonElement(animesPages).jsonArray
        }
    }

    /**
     * Adds an "failed" result to `TESTS_RESULTS_DTO`.
     *
     * @param test The test that failed.
     * @param e The exception returned by the failed test.
     */
    private fun writeTestError(test: TestsEnum, e: Throwable) {
        if (shouldDumpJson) {
            val result = ResultDto(null, e.toString(), false)
            setTestResult(test, result)
        }
    }

    /**
     * Adds an "passed" result to `TESTS_RESULTS_DTO`.
     *
     * @param test The test that ran successfully.
     * @param resultFetcher An lambda block that will return the data generated by the test. It wil run ONLY if `configs.completeResults` is set (--complete-results -C).
     */
    private fun writeTestSuccess(test: TestsEnum, resultFetcher: () -> JsonElement) {
        if (shouldDumpJson) {
            val res = if (!configs.completeResults) null else resultFetcher()
            val result = ResultDto(res)
            setTestResult(test, result)
        }
    }

    /**
     * Sets the value of a property from `TESTS_RESULT_DTO`, based on the test name.
     *
     * @param test The test name / type.
     * @param result The result from the test.
     */
    private fun setTestResult(test: TestsEnum, result: ResultDto) {
        TESTS_RESULTS_DTO.apply {
            when (test) {
                TestsEnum.POPULAR -> popular = result
                TestsEnum.LATEST -> latest = result
                TestsEnum.SEARCH -> search = result
                TestsEnum.ANIDETAILS -> details = result
                TestsEnum.EPLIST -> episodes = result
                TestsEnum.VIDEOLIST -> videos = result
            }
        }
    }

    private suspend inline fun <A, B> Iterable<A>.parallelMap(crossinline f: suspend (A) -> B): List<B> =
        withContext(Dispatchers.Default) {
            map { async { f(it) } }.awaitAll()
        }
}
