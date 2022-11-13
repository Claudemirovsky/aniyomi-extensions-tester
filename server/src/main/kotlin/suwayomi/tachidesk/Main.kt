package suwayomi.tachidesk

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import mu.KotlinLogging
import suwayomi.tachidesk.anime.impl.extension.AnimeExtension
import suwayomi.tachidesk.anime.impl.extension.tester.ExtensionTests
import suwayomi.tachidesk.cmd.CliOptions.parseArgs
import suwayomi.tachidesk.cmd.GREEN
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

    val extensionsInfo = extensions.associate {
        logger.debug("Installing $it")
        val (pkgName, sources) = AnimeExtension.installAPK(tmpDir) { it.toFile() }
        pkgName to buildJsonArray {
            sources.map { source ->
                timeTest("${source.name} TESTS", color = GREEN) {
                    add(
                        buildJsonObject {
                            put("name", source.name)
                            put("result", ExtensionTests(source, options.configs).runTests())
                        }
                    )
                }
                println()
            }
        }
    }

    if (options.jsonFilesDir?.isNotBlank() ?: false) {
        extensionsInfo.map {
            val pkgName = it.key.substringAfter("eu.kanade.tachiyomi.animeextension.")
            val result = it.value.toString()
            File(options.jsonFilesDir).also { it.mkdir() }.also {
                File(it, "results-$pkgName.json").writeText(result)
            }
        }
    }
}
