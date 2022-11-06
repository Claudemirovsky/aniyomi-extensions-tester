package suwayomi.tachidesk.cmd

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video

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
    val pad = if (subPad < 1) 1 else subPad
    println(String.format("%${pad}s", result))
}

fun String.center(width: Int, char: Char = '='): String {
    val pads = width - length
    if (pads <= 0) return this
    val paddedLeft = padStart(length + pads / 2, char)
    val paddedEnd = paddedLeft.padEnd(width, char)
    return paddedEnd
}

fun printTitle(title: String) {
    val newTitle = YELLOW + " $title ".center(70, '=')
        .replaceFirst(" ", " $RESET")
        .replace(" ==", "$YELLOW ==") + RESET
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

fun printEpisode(episode: SEpisode) {
    println()
    printLine("Name", episode.name)
    printLine(
        "Episode number",
        episode.episode_number.toString().trimEnd { it == '0' }.trimEnd { it == '.' }
    )
    printLine("Episode URL", episode.url)
    if (episode.date_upload > 0)
        printLine("Date of upload", episode.date_upload.toString())
    println()
}

fun printVideo(video: Video) {
    println()
    printLine("Quality", video.quality)
    printLine("URL", video.url)
    printLine("Video URL", video.videoUrl)
    video.headers
        ?.also {
            printLine("Video Headers", "")
        }
        ?.forEach { (first, second) ->
            printLine(first, second, subPad = 6)
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
