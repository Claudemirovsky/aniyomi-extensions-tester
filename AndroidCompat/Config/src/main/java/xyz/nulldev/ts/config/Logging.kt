package xyz.nulldev.ts.config

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import ch.qos.logback.classic.Level
import io.github.oshai.kotlinlogging.DelegatingKLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.Logger
import ch.qos.logback.classic.Logger as LogbackLogger

fun setLogLevel(level: Level) {
    val ktlogger = KotlinLogging.logger(Logger.ROOT_LOGGER_NAME)

    @Suppress("UNCHECKED_CAST")
    val slf4jLogger = (ktlogger as DelegatingKLogger<Logger>).underlyingLogger
    (slf4jLogger as LogbackLogger).level = level
}
