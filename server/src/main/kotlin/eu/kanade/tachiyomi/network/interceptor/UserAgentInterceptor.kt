package eu.kanade.tachiyomi.network.interceptor

/*
 * Copyright (C) The Tachiyomi Open Source Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor(
    private val defaultUserAgentProvider: () -> String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        return if (originalRequest.header(USER_AGENT_HEADER).isNullOrEmpty()) {
            val newRequest = originalRequest
                .newBuilder()
                .removeHeader(USER_AGENT_HEADER)
                .addHeader(USER_AGENT_HEADER, defaultUserAgentProvider())
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}

private const val USER_AGENT_HEADER = "User-Agent"
