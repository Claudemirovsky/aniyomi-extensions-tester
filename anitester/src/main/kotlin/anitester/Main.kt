package anitester

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import anitester.cmd.CYAN
import anitester.cmd.CliOptions.parseArgs
import anitester.cmd.GREEN
import anitester.cmd.RED
import anitester.cmd.RESET
import anitester.cmd.printTitle
import anitester.cmd.timeTest
import anitester.tester.ExtensionTests
import anitester.tester.models.SourceResultsDto
import anitester.util.AnimeExtension
import anitester.util.loadCookies
import anitester.util.loadPreferences
import eu.kanade.tachiyomi.App
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.conf.global
import playwright.utils.PlaywrightStatics
import xyz.nulldev.androidcompat.AndroidCompat
import xyz.nulldev.androidcompat.AndroidCompatInitializer
import xyz.nulldev.ts.config.ConfigKodeinModule
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.notExists
import kotlin.streams.toList
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}
private val androidCompat by lazy { AndroidCompat() }

@DelicateCoroutinesApi
@ExperimentalTime
@ExperimentalSerializationApi
suspend fun main(args: Array<String>) {
    val options = parseArgs(args)

    initApplication()
    PlaywrightStatics.useChromium = options.useChromium
    if (options.debugMode) System.setProperty("ANIEXT_TESTER_DEBUG", "true")
    options.userAgent?.let { System.setProperty("http.agent", it) }
    options.proxy?.let { System.setProperty("ANIEXT_TESTER_PROXY", it) }

    options.cookiesFile?.let { loadCookies(Paths.get(it)) }
    options.preferencesFile?.let { loadPreferences(Paths.get(it)) }

    val apksPath = Paths.get(options.apksPath)
    if (apksPath.notExists()) {
        println("${RED}ERROR: Path \"$apksPath\" does not exist. $RESET")
        exitProcess(1)
    }

    val tmpDir = File(options.tmpDir, "aniyomi-extension-tester").also(File::mkdir)

    val extensions = if (apksPath.extension == "apk") {
        listOf(apksPath)
    } else {
        Files.find(
            apksPath,
            2,
            { _, fileAttributes -> fileAttributes.isRegularFile },
        )
            .filter { it.extension == "apk" }
            .toList()
            .ifEmpty {
                println("${RED}ERROR: No .apk file was found inside \"$apksPath\" path.$RESET")
                exitProcess(1)
            }
    }

    val json = Json {
        prettyPrint = options.prettyJson
        explicitNulls = false
    }

    extensions.forEachIndexed { index, ext ->
        logger.debug { "Installing $ext" }
        val (pkgName, sources) = AnimeExtension.installApk(tmpDir) { ext.toFile() }
        val shouldDumpJson = !options.jsonFilesDir.isNullOrBlank()
        val results = sources.map { source ->
            timeTest("$source TESTS", color = GREEN) {
                val res = ExtensionTests(source, options.configs, shouldDumpJson).runTests()
                println()
                SourceResultsDto(source.toString(), res)
            }
        }
        println()
        printTitle("${index + 1}/${extensions.size} EXTENSIONS DONE.", CYAN)
        println()

        if (shouldDumpJson) {
            val name = pkgName.substringAfter("eu.kanade.tachiyomi.animeextension.")
            val result = json.encodeToString(results)

            File(options.jsonFilesDir).also(File::mkdir).also {
                File(it, "results-$name.json").writeText(result)
            }
        }
    }

    if (PlaywrightStatics.usedPlaywright) {
        PlaywrightStatics.playwrightInstance.close()
    }

    exitProcess(0)
}

internal fun initApplication() {
    logger.info { "Running Aniyomi-Extensions-Tester ${BuildConfig.VERSION} revision ${BuildConfig.REVISION}" }

    // Load config API
    DI.global.addImport(ConfigKodeinModule().create())
    // Load Android compatibility dependencies
    AndroidCompatInitializer().init()
    // start app
    androidCompat.startApp(App())
}
