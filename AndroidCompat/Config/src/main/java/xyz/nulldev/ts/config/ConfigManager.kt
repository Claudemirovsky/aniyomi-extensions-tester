package xyz.nulldev.ts.config

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 */

import ch.qos.logback.classic.Level
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Manages app config.
 */
open class ConfigManager {
    private val generatedModules = mutableMapOf<Class<out ConfigModule>, ConfigModule>()
    val config by lazy { loadConfigs() }

    // Public read-only view of modules
    val loadedModules: Map<Class<out ConfigModule>, ConfigModule>
        get() = generatedModules

    val logger = KotlinLogging.logger {}

    /**
     * Get a config module
     */
    inline fun <reified T : ConfigModule> module(): T = loadedModules[T::class.java] as T

    /**
     * Get a config module (Java API)
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ConfigModule> module(type: Class<T>): T = loadedModules[type] as T

    /**
     * Load configs
     */
    fun loadConfigs(): Config {
        // Load reference configs
        val compatConfig = ConfigFactory.parseResources("compat-reference.conf")

        val config = ConfigFactory.empty()
            .withFallback(compatConfig)
            .resolve()

        // set log level early
        setLogLevel(Level.DEBUG)

        return config
    }

    fun registerModule(module: ConfigModule) {
        generatedModules[module.javaClass] = module
    }

    fun registerModules(vararg modules: ConfigModule) {
        modules.forEach(::registerModule)
    }
}

object GlobalConfigManager : ConfigManager()
