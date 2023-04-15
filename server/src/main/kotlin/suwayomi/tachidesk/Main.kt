package suwayomi.tachidesk

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import suwayomi.tachidesk.anime.impl.extension.AnimeExtension
import suwayomi.tachidesk.anime.impl.extension.tester.ExtensionTests
import suwayomi.tachidesk.anime.impl.extension.tester.models.SourceResultsDto
import suwayomi.tachidesk.cmd.CYAN
import suwayomi.tachidesk.cmd.CliOptions.parseArgs
import suwayomi.tachidesk.cmd.GREEN
import suwayomi.tachidesk.cmd.RED
import suwayomi.tachidesk.cmd.RESET
import suwayomi.tachidesk.cmd.printTitle
import suwayomi.tachidesk.cmd.timeTest
import suwayomi.tachidesk.server.applicationSetup
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.notExists
import kotlin.streams.asSequence
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalSerializationApi
suspend fun main(args: Array<String>) {
    applicationSetup()

    val options = parseArgs(args)

    if (options.debugMode) System.setProperty("ANIEXT_TESTER_DEBUG", "true")
    options.userAgent?.let { System.setProperty("http.agent", it) }
    options.proxy?.let { System.setProperty("ANIEXT_TESTER_PROXY", it) }

    val apksPath = Paths.get(options.apksPath)
    if (apksPath.notExists()) {
        println("${RED}ERROR: Path \"$apksPath\" does not exist. $RESET")
        return
    }

    val tmpDir = File(options.tmpDir, "aniyomi-extension-tester").also(File::mkdir)

    val extensions = if (apksPath.extension == "apk") {
        listOf(apksPath)
    } else {
        Files.find(
            apksPath,
            2,
            { _, fileAttributes -> fileAttributes.isRegularFile }
        )
            .asSequence()
            .filter { it.extension == "apk" }
            .toList()
            .ifEmpty {
                println("${RED}ERROR: No .apk file was found inside \"$apksPath\" path.$RESET")
                return
            }
    }

    val json = Json { prettyPrint = options.prettyJson; explicitNulls = false }

    extensions.forEachIndexed { index, ext ->
        logger.debug("Installing $ext")
        val (pkgName, sources) = AnimeExtension.installAPK(tmpDir) { ext.toFile() }
        val results = sources.map { source ->
            timeTest("${source.name} TESTS", color = GREEN) {
                val res = ExtensionTests(source, options.configs).runTests()
                println()
                SourceResultsDto(source.name, res)
            }
        }
        println()
        printTitle("${index + 1}/${extensions.size} EXTENSIONS DONE.", CYAN)
        println()

        if (options.jsonFilesDir?.isNotBlank() ?: false) {
            val name = pkgName.substringAfter("eu.kanade.tachiyomi.animeextension.")
            val result = json.encodeToString(results)

            File(options.jsonFilesDir).also(File::mkdir).also {
                File(it, "results-$name.json").writeText(result)
            }
        }
    }
}
