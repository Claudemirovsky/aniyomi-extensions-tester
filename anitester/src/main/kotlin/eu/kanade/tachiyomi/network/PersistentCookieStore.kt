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
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import java.net.URI

// from TachiWeb-Server
class PersistentCookieStore : StubbedCookieManager() {

    @Synchronized
    fun removeAll() = removeAllCookies() {}

    fun remove(uri: URI) {
        preferences.edit().remove(uri.host).apply()
        cookieMap.remove(uri.host)
    }

    fun get(url: HttpUrl) = get(url.host)

    fun get(uri: URI) = get(uri.host)

    private fun get(url: String): List<Cookie> {
        return cookieMap[url].orEmpty()
    }
}
