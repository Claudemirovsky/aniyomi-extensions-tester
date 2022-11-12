package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import android.content.Context
import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import eu.kanade.tachiyomi.network.interceptor.UserAgentInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

class NetworkHelper(context: Context) {

    val cookieManager = MemoryCookieJar()

    val client by lazy {
        val builder = OkHttpClient.Builder()
            .cookieJar(cookieManager)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(UserAgentInterceptor())
        System.getProperty("ANIEXT_TESTER_PROXY")?.let {
            parseProxy(it)?.let { proxy ->
                builder.proxy(proxy)
                builder.ignoreAllSSLErrors()
            }
        }
        if (System.getProperty("ANIEXT_TESTER_DEBUG")?.equals("true") ?: false) {
            val loggingInterceptor = HttpLoggingInterceptor(
                object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) = println(message)
                }
            ).apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            builder.addInterceptor(loggingInterceptor)
        }
        builder.build()
    }

    val cloudflareClient by lazy {
        client.newBuilder()
            .addInterceptor(CloudflareInterceptor())
            .build()
    }

    val defaultUserAgent by lazy {
        System.getProperty("ANIEXT_TESTER_UA")
            ?: "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:106.0) Gecko/20100101 Firefox/106.0"
    }

    private fun parseProxy(proxy: String): Proxy? {
        if (proxy.isBlank()) return null
        val port = proxy.substringAfterLast(":").toIntOrNull() ?: return null
        val host = proxy.substringBeforeLast(":").substringAfter("://")
        val type = if (proxy.substringBefore("://").startsWith("socks")) {
            Proxy.Type.SOCKS
        } else {
            Proxy.Type.HTTP
        }

        return Proxy(type, InetSocketAddress(host, port))
    }
}
