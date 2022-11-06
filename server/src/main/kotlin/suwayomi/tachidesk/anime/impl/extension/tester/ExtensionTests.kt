package suwayomi.tachidesk.anime.impl.extension.tester

import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnimeImpl
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import rx.Observable
import suwayomi.tachidesk.cmd.RED
import suwayomi.tachidesk.cmd.RESET
import suwayomi.tachidesk.cmd.dto.ConfigsDto
import suwayomi.tachidesk.cmd.printAnime
import suwayomi.tachidesk.cmd.printLine
import suwayomi.tachidesk.cmd.printTitle
import kotlin.system.exitProcess

class ExtensionTests(
    private val source: AnimeHttpSource,
    private val configs: ConfigsDto
) {
    private val logger = KotlinLogging.logger {}

    private val json = Json { prettyPrint = true }

    private var ANIDETAILS_URL: String = configs.animeUrl
    private var EP_URL: String = configs.episodeUrl

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
                    } /*
                    TestsEnum.SEARCH -> testSearchAnimesPage()
                    TestsEnun.ANIDETAILS -> testAnimeDetails()
                    TestsEnum.EPLIST -> testEpisodeList()
                    TestsEnum.VIDEOLIST -> testVideoList()*/
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

    private fun testLatestAnimesPage() {
        printAnimesPage("LATEST ANIMES PAGE") { page: Int ->
            source.fetchLatestUpdates(page)
        }
    }

    private fun testPopularAnimesPage() {
        printAnimesPage("POPULAR ANIMES PAGE") { page: Int ->
            source.fetchPopularAnime(page)
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
                    it.take(3)
                else it
            }

            animes.forEach {
                if (configs.printJson) {
                    val anime = it as SAnimeImpl
                    println(json.encodeToString(anime))
                } else {
                    printAnime(it)
                }
            }
            if (!configs.increment || !results.hasNextPage || page >= 2) break
            else println("${RED}Incrementing page number$RESET")
        }
        printTitle("END $title")
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
