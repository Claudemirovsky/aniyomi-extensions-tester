package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import android.content.Context
import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import eu.kanade.tachiyomi.network.interceptor.UncaughtExceptionInterceptor
import eu.kanade.tachiyomi.network.interceptor.UserAgentInterceptor
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import playwright.utils.PlaywrightStatics
import java.util.concurrent.TimeUnit

@Suppress("UNUSED_PARAMETER")
class NetworkHelper(context: Context) {

    val cookieManager = PersistentCookieJar()

    val client by lazy {
        val builder = OkHttpClient.Builder()
            .cookieJar(cookieManager)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(BrotliInterceptor)
            .addInterceptor(UncaughtExceptionInterceptor())
            .addInterceptor(UserAgentInterceptor(::defaultUserAgentProvider))
            .addInterceptor(CloudflareInterceptor(cookies))

        System.getProperty("ANIEXT_TESTER_PROXY")
            ?.let(::parseProxy)
            ?.also {
                // We usually use proxies to debug https requests, so let's
                // prevent some headache
                builder.ignoreAllSSLErrors()
            }

        if (System.getProperty("ANIEXT_TESTER_DEBUG")?.equals("true") == true) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                // using logging makes it almost unreadable, so we're using
                // plain println instead
                println(message)
            }.apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            builder.addInterceptor(loggingInterceptor)
        }
        builder.build()
    }

    /*
     * @deprecated Since extension-lib 15
     */
    @Deprecated("The regular client handles Cloudflare by default")
    @Suppress("UNUSED")
    val cloudflareClient: OkHttpClient = client

    private val defaultUserAgent by lazy {
        System.getProperty("http.agent")
            ?: PlaywrightStatics.userAgent
    }

    fun defaultUserAgentProvider() = defaultUserAgent

    /**
     * Parses a proxy address and uses it if its valid.
     *
     * @param proxy The http/https/socks5 proxy address
     */
    private fun parseProxy(proxy: String): Boolean? {
        if (proxy.isBlank()) return null
        val port = proxy.substringAfterLast(":")
        if (port.any { !it.isDigit() }) return null
        val host = proxy.substringBeforeLast(":").substringAfter("://")
        val type = proxy.substringBefore("://").let {
            // Adds a dot to every type, except socks[x]
            if ("socks" in it) {
                "socks"
            } else {
                it + "."
            }
        }
        System.setProperty("${type}ProxyHost", host)
        System.setProperty("${type}ProxyPort", port)
        return true
    }

    val cookies: PersistentCookieStore
        get() = cookieManager.store
}
