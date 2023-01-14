package suwayomi.tachidesk.cmd

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import suwayomi.tachidesk.anime.impl.extension.tester.models.TestsEnum
import java.text.SimpleDateFormat
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val RED = "\u001B[91;1m"
const val GREEN = "\u001B[92;1m"
const val YELLOW = "\u001B[93;1m"
const val CYAN = "\u001B[96;1m"
const val RESET = "\u001B[0m"

/**
 * Print a line with a key and value.
 *
 * @param first The first item (or key).
 * @param second The secone item (or value), if its null it will not be printed.
 * @param width The width of the line.
 * @param subPad Amount of initial padding, useful for sub-items.
 * @param color A terminal-color string for the value.
 */
fun printLine(
    first: String,
    second: String?,
    width: Int = 17,
    subPad: Int = 1,
    color: String = CYAN
) {
    if (second == null) return

    val paddedFirst = first.padEnd(width)
    val paddedSecond = "-> $second".padStart(width - paddedFirst.length)
    val result = GREEN + paddedFirst + paddedSecond
        .replaceFirst("->", "$RESET->$color") + RESET

    val paddedResult =
        if (subPad <= 1) {
            result
        } else {
            String.format("%${subPad}s%s", "", result)
        }

    println(paddedResult)
}

/**
 * Centers a string between some amount of a character.
 *
 * @param width The total width/size of the line.
 * @param char The character to use in the sides.
 */
fun String.center(width: Int, char: Char = '='): String {
    val pads = width - length
    if (pads <= 0) return this
    val paddedLeft = padStart(length + pads / 2, char)
    val paddedEnd = paddedLeft.padEnd(width, char)
    return paddedEnd
}

/**
 * Prints a title to each step.
 *
 * @param title The title / separator
 * @param barColor The color for the padding bars
 */
fun printTitle(title: String, barColor: String = YELLOW) {
    val newTitle = barColor + " $title ".center(70, '=')
        .replaceFirst(" ", " $RESET")
        .replace(" ==", "$barColor ==") + RESET
    println(newTitle)
}

/**
 * Pretty-prints a SAnime instance.
 *
 * @param anime The SAnime / SAnimeImpl object.
 * @param checkThumb A boolean that enables the printing of thumbnail status.
 */
fun printAnime(anime: SAnime, checkThumb: Boolean = false) {
    println()
    printLine("Title", anime.title)
    printLine("Anime URL", anime.url)
    printLine("Thumbnail URL", anime.thumbnail_url)
    if (checkThumb) {
        printIfWorks(anime.is_thumbnail_loading, "Thumbnail loads?")
    }
    printLine("Status", SAnime.getStatus(anime.status))
    printLine("Artist", anime.artist)
    printLine("Author", anime.author)
    printLine("Genres", anime.genre)
    printLine("Description", anime.description)
}

/**
 * Pretty-prints a SEpisode / SEpisodeImpl object.
 *
 * @param episode The SEpisode / SEpisodeImpl instance.
 * @param formatter The date formatter for the date of upload.
 */
fun printEpisode(episode: SEpisode, formatter: SimpleDateFormat) {
    println()
    printLine("Name", episode.name)
    printLine(
        "Episode number",
        episode.episode_number.toString().trimEnd { it == '0' }.trimEnd { it == '.' }
    )
    printLine("Episode URL", episode.url)
    if (episode.date_upload > 0) {
        printLine("Date of upload", formatter.format(episode.date_upload))
    }
}

/**
 * Pretty-prints a Video object
 *
 * @param video The Video instance
 */
fun printVideo(video: Video) {
    println()
    printLine("Quality", video.quality)
    printIfWorks(video.isWorking, "Is playing?")
    printLine("Video URL", video.videoUrl)
    if (video.url != video.videoUrl) {
        printLine("URL", video.url)
    }

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

/**
 * Prints the status of a boolean.
 *
 * @param value The boolean value.
 * @param title The title before the status.
 */
fun printIfWorks(value: Boolean, title: String) {
    if (value) {
        printLine(title, "YES", color = GREEN)
    } else {
        printLine(title, "NO", color = RED)
    }
}

/**
 * Prints the time spent on a test, using the TestsEnum to get its title.
 *
 * @param test The TestsEnum item.
 * @param testBlock The function / lambda block to run and be timed.
 */
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

/**
 * Prints the time spent on a function, with a title/separator to give context.
 *
 * @param title The title/separator.
 * @param color The color of the padding bars.
 * @param testBlock The function/lambda block to be timed.
 */
@ExperimentalTime
inline fun <T> timeTest(title: String, color: String = YELLOW, testBlock: () -> T): T {
    println()
    printTitle("STARTING $title", color)
    return measureTimedValue(testBlock).also {
        val secs = it.duration.toDouble(DurationUnit.SECONDS)
        println()
        printTitle("COMPLETED $title IN ${String.format("%.2f", secs)}s", color)
    }.value
}
