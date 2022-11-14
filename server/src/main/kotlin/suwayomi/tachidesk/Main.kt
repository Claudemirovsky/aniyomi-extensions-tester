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
import suwayomi.tachidesk.cmd.printTitle
import suwayomi.tachidesk.cmd.timeTest
import suwayomi.tachidesk.server.applicationSetup
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.extension
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

    val apksPath = options.apksPath

    val tmpDir = File(options.tmpDir, "aniyomi-extension-tester").also { it.mkdir() }

    val extensions = if (apksPath.endsWith(".apk")) {
        listOf(Paths.get(apksPath))
    } else {
        Files.find(
            Paths.get(apksPath),
            2,
            { _, fileAttributes -> fileAttributes.isRegularFile }
        )
            .asSequence()
            .filter { it.extension == "apk" }
            .toList()
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

            File(options.jsonFilesDir).also { it.mkdir() }.also {
                File(it, "results-$name.json").writeText(result)
            }
        }
    }
}
