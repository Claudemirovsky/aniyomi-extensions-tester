package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

// from TachiWeb-Server
class PersistentCookieJar : CookieJar {

    val store = PersistentCookieStore()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        store.addAll(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return store.get(url)
    }
}
