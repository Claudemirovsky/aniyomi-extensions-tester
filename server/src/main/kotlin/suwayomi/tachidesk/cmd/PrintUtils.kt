package suwayomi.tachidesk.cmd

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import suwayomi.tachidesk.anime.impl.extension.tester.TestsEnum
import java.text.SimpleDateFormat
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val RED = "\u001B[91;1m"
const val GREEN = "\u001B[92;1m"
const val YELLOW = "\u001B[93;1m"
const val CYAN = "\u001B[96;1m"
const val RESET = "\u001B[0m"

fun printLine(
    first: String,
    second: String?,
    width: Int = 15,
    subPad: Int = 1,
    color: String = CYAN
) {
    if (second == null) return

    val paddedFirst = first.padEnd(width)
    val paddedSecond = "-> $second".padStart(width - paddedFirst.length)
    val result = GREEN + paddedFirst + paddedSecond
        .replaceFirst("->", "$RESET->$color") + RESET

    val paddedResult =
        if (subPad <= 1) result
        else String.format("%${subPad}s%s", "", result)

    println(paddedResult)
}

fun String.center(width: Int, char: Char = '='): String {
    val pads = width - length
    if (pads <= 0) return this
    val paddedLeft = padStart(length + pads / 2, char)
    val paddedEnd = paddedLeft.padEnd(width, char)
    return paddedEnd
}

fun printTitle(title: String, barColor: String = YELLOW) {
    val newTitle = barColor + " $title ".center(70, '=')
        .replaceFirst(" ", " $RESET")
        .replace(" ==", "$barColor ==") + RESET
    println(newTitle)
}

fun printAnime(anime: SAnime) {
    println()
    printLine("Title", anime.title)
    printLine("Anime URL", anime.url)
    printLine("Thumbnail URL", anime.thumbnail_url)
    printLine("Status", SAnime.getStatus(anime.status))
    printLine("Artist", anime.artist)
    printLine("Author", anime.author)
    printLine("Genres", anime.genre)
    printLine("Description", anime.description)
}

fun printEpisode(episode: SEpisode, formatter: SimpleDateFormat) {
    println()
    printLine("Name", episode.name)
    printLine(
        "Episode number",
        episode.episode_number.toString().trimEnd { it == '0' }.trimEnd { it == '.' }
    )
    printLine("Episode URL", episode.url)
    if (episode.date_upload > 0)
        printLine("Date of upload", formatter.format(episode.date_upload))
}

fun printVideo(video: Video) {
    println()
    printLine("Quality", video.quality)
    printLine("Video URL", video.videoUrl)
    if (video.url != video.videoUrl)
        printLine("URL", video.url)

    if (video.isWorking) printLine("IS WORKING", "YES", color = GREEN)
    else printLine("IS WORKING", "NO", color = RED)

    video.headers
        ?.also {
            printLine("Video Headers", "")
        }
        ?.forEach { (first, second) ->
            printLine(first, second, width = 25, subPad = 6)
        }
    if (video.subtitleTracks.size > 0) {
        printLine("Subs", "")
        video.subtitleTracks.forEach {
            printLine("Sub Lang", it.lang, subPad = 6)
            printLine("Sub URL", it.url, subPad = 6)
        }
    }
}

@ExperimentalTime
fun timeTestFromEnum(test: TestsEnum, testBlock: () -> Unit) {
    val title = when (test) {
        TestsEnum.ANIDETAILS -> "ANIME DETAILS"
        TestsEnum.EPLIST -> "EPISODE LIST"
        TestsEnum.VIDEOLIST -> "VIDEO LIST"
        else -> "${test.name} PAGE"
    } + " TEST"
    timeTest(title, YELLOW, testBlock)
}

@ExperimentalTime
fun timeTest(title: String, color: String = YELLOW, testBlock: () -> Unit) {
    println()
    printTitle("STARTING $title", color)
    measureTimedValue(testBlock).also {
        val secs = it.duration.toDouble(DurationUnit.SECONDS)
        println()
        printTitle("COMPLETED $title IN ${String.format("%.2f", secs)}s", color)
    }
}
