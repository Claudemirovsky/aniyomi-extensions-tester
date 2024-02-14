package anitester.util

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import anitester.util.PackageTools.EXTENSION_FEATURE
import anitester.util.PackageTools.LIB_VERSION_MAX
import anitester.util.PackageTools.LIB_VERSION_MIN
import anitester.util.PackageTools.METADATA_SOURCE_CLASS
import anitester.util.PackageTools.dex2jar
import anitester.util.PackageTools.getPackageInfo
import anitester.util.PackageTools.loadExtensionSources
import eu.kanade.tachiyomi.animesource.AnimeSource
import eu.kanade.tachiyomi.animesource.AnimeSourceFactory
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object AnimeExtension {
    private val logger = KotlinLogging.logger {}

    suspend fun installApk(
        tmpDir: File,
        fetcher: suspend () -> File,
    ): Pair<String, List<AnimeHttpSource>> {
        val apkFile = fetcher()

        val jarFile = File(tmpDir, "${apkFile.nameWithoutExtension}.jar")
            .also(File::deleteOnExit)

        val packageInfo = getPackageInfo(apkFile.absolutePath)

        if (!packageInfo.reqFeatures.orEmpty().any { it.name == EXTENSION_FEATURE }) {
            throw Exception("This apk is not a Aniyomi extension")
        }

        // Validate lib version
        val libVersion = packageInfo.versionName.substringBeforeLast('.').toDouble()
        if (libVersion < LIB_VERSION_MIN || libVersion > LIB_VERSION_MAX) {
            throw Exception(
                "Lib version is $libVersion, while only versions " +
                    "$LIB_VERSION_MIN to $LIB_VERSION_MAX are allowed",
            )
        }

        val className = packageInfo.packageName + packageInfo.applicationInfo.metaData.getString(
            METADATA_SOURCE_CLASS,
        )

        logger.trace { "Main class for extension is $className" }

        dex2jar(apkFile, jarFile)
        extractAssetsFromApk(apkFile, jarFile)

        // collect sources from the extension
        return packageInfo.packageName to when (val instance = loadExtensionSources(jarFile, className)) {
            is AnimeSource -> listOf(instance).filterIsInstance<AnimeHttpSource>()
            is AnimeSourceFactory -> instance.createSources().filterIsInstance<AnimeHttpSource>()
            else -> throw RuntimeException("Unknown source class type! ${instance.javaClass}")
        }
    }

    private fun extractAssetsFromApk(apkFile: File, jarFile: File) {
        initFileSystem(apkFile)?.use { apkfs ->
            val assets = apkfs.getPath("assets")
            if (Files.exists(assets)) {
                initFileSystem(jarFile)?.use { zipfs ->
                    val jarRoot = zipfs.getPath("/")
                    Files.walk(assets).forEach { assetFile ->
                        val dest = jarRoot.resolve(assetFile)
                        try {
                            Files.copy(assetFile, dest, StandardCopyOption.REPLACE_EXISTING)
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    private fun initFileSystem(file: File): FileSystem? {
        val uri = URI("jar:${file.toURI()}!/")
        return try {
            FileSystems.newFileSystem(uri, emptyMap<String, String>())
        } catch (_: Exception) { null }
    }
}
