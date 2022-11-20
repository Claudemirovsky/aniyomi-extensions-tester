package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import java.net.HttpCookie
import java.net.URI

// from TachiWeb-Server
class PersistentCookieStore {

    private val cookieManager = StubbedCookieManager()

    @Synchronized
    fun addAll(url: HttpUrl, cookies: List<Cookie>) {
        val key = url.toUri()
        cookies.forEach {
            val cookie = it.toHttpCookie().toString()
            cookieManager.setCookie(key, cookie)
        }
    }

    @Synchronized
    fun removeAll() {
        cookieManager.removeAllCookie()
    }

    fun remove(uri: URI) {
        cookieManager.prefs.edit().remove(uri.host).apply()
        cookieManager.cookieMap.remove(uri.host)
    }

    fun get(url: HttpUrl) = get(url.toUri().host)

    fun get(uri: URI) = get(uri.host)

    private fun get(url: String): List<Cookie> {
        val newUrl = url.toHttpUrlOrNull() ?: return emptyList<Cookie>()
        return cookieManager.getCookie(url).split(";").mapNotNull {
            Cookie.parse(newUrl, it)
        }
    }

    private fun Cookie.toHttpCookie() = HttpCookie(name, value)
}
