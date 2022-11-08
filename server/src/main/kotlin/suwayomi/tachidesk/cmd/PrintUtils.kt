package suwayomi.tachidesk.cmd

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import java.text.SimpleDateFormat

const val RED = "\u001B[91;1m"
const val GREEN = "\u001B[92;1m"
const val YELLOW = "\u001B[93;1m"
const val CYAN = "\u001B[96;1m"
const val RESET = "\u001B[0m"

fun printLine(first: String, second: String?, width: Int = 15, subPad: Int = 1) {
    if (second == null) return

    val paddedFirst = first.padEnd(width)
    val paddedSecond = "-> $second".padStart(width - paddedFirst.length)
    val result = GREEN + paddedFirst + paddedSecond
        .replaceFirst("->", "$RESET->$CYAN") + RESET

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
    println()
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
    println()
}

fun printVideo(video: Video) {
    println()
    printLine("Quality", video.quality)
    printLine("Video URL", video.videoUrl)
    if (video.url != video.videoUrl)
        printLine("URL", video.url)
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

    println()
}