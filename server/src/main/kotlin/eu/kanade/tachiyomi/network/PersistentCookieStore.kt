package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import android.content.Context
import okhttp3.Cookie
import okhttp3.HttpUrl
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import xyz.nulldev.androidcompat.androidimpl.hasExpired
import java.net.URI

// from TachiWeb-Server
class PersistentCookieStore(context: Context) {

    private val cookieMap by lazy { StubbedCookieManager.cookieMap }
    private val prefs by lazy { StubbedCookieManager.preferences }

    @Synchronized
    fun addAll(url: HttpUrl, cookies: List<Cookie>) {
        val key = url.toUri().host

        // Append or replace the cookies for this domain.
        val cookiesForDomain = cookieMap[key].orEmpty().toMutableList()
        for (cookie in cookies) {
            // Find a cookie with the same name. Replace it if found, otherwise add a new one.
            val pos = cookiesForDomain.indexOfFirst { it.name == cookie.name }
            if (pos == -1) {
                cookiesForDomain.add(cookie)
            } else {
                cookiesForDomain[pos] = cookie
            }
        }
        cookieMap.put(key, cookiesForDomain)

        // Get cookies to be stored in disk
        val newValues = cookiesForDomain.asSequence()
            .filter { it.persistent && !it.hasExpired() }
            .map(Cookie::toString)
            .toSet()

        prefs.edit().putStringSet(key, newValues).apply()
    }

    @Synchronized
    fun removeAll() {
        prefs.edit().clear().apply()
        cookieMap.clear()
    }

    fun remove(uri: URI) {
        prefs.edit().remove(uri.host).apply()
        cookieMap.remove(uri.host)
    }

    fun get(url: HttpUrl) = get(url.host)

    fun get(uri: URI) = get(uri.host)

    private fun get(url: String): List<Cookie> {
        return cookieMap[url].orEmpty().filter { !it.hasExpired() }
    }
}
