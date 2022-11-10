package eu.kanade.tachiyomi.network.interceptor

/*
 * Copyright (C) The Tachiyomi Open Source Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import eu.kanade.tachiyomi.network.NetworkHelper
import okhttp3.Interceptor
import okhttp3.Response
import uy.kohesive.injekt.injectLazy

class UserAgentInterceptor : Interceptor {

    private val networkHelper: NetworkHelper by injectLazy()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        return if (originalRequest.header("User-Agent").isNullOrEmpty()) {
            val newRequest = originalRequest
                .newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", networkHelper.defaultUserAgent)
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
