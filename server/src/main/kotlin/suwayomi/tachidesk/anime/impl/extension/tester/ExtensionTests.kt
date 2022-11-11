package suwayomi.tachidesk.anime.impl.extension.tester

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.AnimesPageDto
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SAnimeImpl
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SEpisodeImpl
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.model.VideoDto
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import eu.kanade.tachiyomi.network.HEAD
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import mu.KotlinLogging
import okhttp3.Headers
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

class FailedTestException(error: Throwable) : Exception(error) {
    constructor(error: String = "") : this(Exception(error))
}

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

    private var TEST_RESULT_POPULAR: JsonObject? = null
    private var TEST_RESULT_LATEST: JsonObject? = null
    private var TEST_RESULT_SEARCH: JsonObject? = null
    private var TEST_RESULT_ANIDETAILS: JsonObject? = null
    private var TEST_RESULT_EPLIST: JsonObject? = null
    private var TEST_RESULT_VIDEOLIST: JsonObject? = null

    private val tests by lazy {
        val list = configs.tests.split(",")
        list.mapNotNull {
            runCatching {
                TestsEnum.valueOf(it.uppercase())
            }.getOrNull()
        }
    }

    @ExperimentalTime
    fun runTests(): JsonObject {
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
                writeTestError(test, e)
                printTitle("${test.name} TEST FAILED", barColor = RED)
                if (configs.stopOnError)
                    exitProcess(-1)
            } catch (e: Exception) {
                writeTestError(test, e)
                logger.error("Test($test): ", e)
                if (configs.stopOnError)
                    exitProcess(-1)
            }
        }
        return testResults()
    }

    private fun testPopularAnimesPage() {
        TEST_RESULT_POPULAR = printAnimesPage() { page: Int ->
            source.fetchPopularAnime(page)
        }
    }

    private fun testLatestAnimesPage() {
        TEST_RESULT_LATEST = printAnimesPage() { page: Int ->
            source.fetchLatestUpdates(page)
        }
    }

    private fun testSearchAnimesPage() {
        TEST_RESULT_SEARCH = printAnimesPage() { page: Int ->
            source.fetchSearchAnime(page, configs.searchStr, AnimeFilterList())
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
        TEST_RESULT_ANIDETAILS = resultObject(json.encodeToJsonElement(details as SAnimeImpl).jsonObject)
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
            val sEpisodeImplList = episodeList.map { it as SEpisodeImpl }
            TEST_RESULT_EPLIST = resultObject(json.encodeToJsonElement(sEpisodeImplList).jsonArray)
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
            throw FailedTestException()

        videoList.forEach {
            val test = testMediaResult(it.videoUrl ?: it.url, true, it.headers)
            it.isWorking = test
            printItemOrJson<Video>(it)
        }
        val videoDtoList = videoList.map { VideoDto(it) }
        TEST_RESULT_VIDEOLIST = resultObject(json.encodeToJsonElement(videoDtoList).jsonArray)
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
            is SAnime -> printAnime(item, configs.checkThumbnails)
            is SEpisode -> printEpisode(item, DATE_FORMATTER)
            is Video -> printVideo(item)
        }
    }

    private fun testMediaResult(
        url: String?,
        video: Boolean = false,
        headers: Headers? = null
    ): Boolean {
        if (url == null) return false
        val newHeaders = Headers.Builder().apply {
            addAll(headers ?: source.headers)
            add("Range", "bytes=0-1")
        }.build()

        val req = source.client.newCall(HEAD(url, newHeaders)).execute()
        if (!req.isSuccessful) return false

        val resType = req.header("content-type", "") ?: ""
        val mimeTypes = listOf("video/", "/x-mpegURL", "/vnd.apple.mpegurl")
        return if (video) mimeTypes.any { it in resType } else "image/" in resType
    }

    private fun getSAnime(): SAnime {
        return ANIME_OBJ ?: SAnime.create().apply {
            url = ANIDETAILS_URL
        }
    }

    private fun printAnimesPage(block: (page: Int) -> Observable<AnimesPage>): JsonObject {
        var page = 0
        val animesPages = mutableListOf<AnimesPageDto>()
        while (true) {
            page++
            val results = parseObservable<AnimesPage>(
                block(page)
            )
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
                if (ANIDETAILS_URL.isBlank() && ANIME_OBJ == null)
                    ANIME_OBJ = it
                if (configs.checkThumbnails)
                    it.is_thumbnail_loading = testMediaResult(it.thumbnail_url)
                printItemOrJson<SAnime>(it)
            }

            if (!configs.increment || !results.hasNextPage || page >= 2) break
            else println("${RED}Incrementing page number$RESET")
        }
        return resultObject(json.encodeToJsonElement(animesPages).jsonArray)
    }

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

    private fun testResults(): JsonObject {
        return buildJsonObject {
            TEST_RESULT_POPULAR?.let { put("popular", it) }
            TEST_RESULT_LATEST?.let { put("latest", it) }
            TEST_RESULT_SEARCH?.let { put("search", it) }
            TEST_RESULT_ANIDETAILS?.let { put("details", it) }
            TEST_RESULT_EPLIST?.let { put("episodes", it) }
            TEST_RESULT_VIDEOLIST?.let { put("videos", it) }
        }
    }

    private fun writeTestError(test: TestsEnum, e: Exception) {
        val res = resultObject(null, e, false)
        when (test) {
            TestsEnum.POPULAR -> {
                TEST_RESULT_POPULAR = res
            }
            TestsEnum.LATEST -> {
                TEST_RESULT_LATEST = res
            }
            TestsEnum.SEARCH -> {
                TEST_RESULT_SEARCH = res
            }
            TestsEnum.ANIDETAILS -> {
                TEST_RESULT_ANIDETAILS = res
            }
            TestsEnum.EPLIST -> {
                TEST_RESULT_EPLIST = res
            }
            TestsEnum.VIDEOLIST -> {
                TEST_RESULT_VIDEOLIST = res
            }
        }
    }

    private fun resultObject(result: JsonElement? = null, error: Exception? = null, passed: Boolean = true) = buildJsonObject {
        put("passed", passed)
        if (passed && result != null) {
            put("result", result)
        } else if (!passed && error != null) {
            put("error", error.toString())
        }
    }
}
