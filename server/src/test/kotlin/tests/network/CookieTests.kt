package tests.network

import android.webkit.CookieManager
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.PersistentCookieStore
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import tests.util.AnitesterTest
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CookieTests : AnitesterTest() {
    private val cookieManager by lazy { CookieManager.getInstance() }
    private val networkStorage by lazy { PersistentCookieStore() }
    private val network: NetworkHelper by injectLazy()
    private val url = "https://example.org.ru"
    private val cookies = setOf("test=something", "another=yes")

    @BeforeTest
    fun clearCookies() {
        cookieManager.removeAllCookies() {}
        assert(StubbedCookieManager.cookieMap.isEmpty())
    }

    @Test fun `Test CookieManager instance`() {
        cookies.forEach { cookieManager.setCookie(url, it) }
        assertEquals(cookieManager.getCookie(url), cookies.joinToString("; "))
    }

    @Test fun `Test PersistentCookieStore instance`() {
        val httpUrl = url.toHttpUrl()
        val httpCookies = cookies.mapNotNull { Cookie.parse(httpUrl, it) }
        networkStorage.addAll(httpUrl, httpCookies)
        assertEquals(networkStorage.get(httpUrl), httpCookies)
    }

    @Test fun `Verify interoperability between CookieManager and PersistentCookieStore`() {
        cookies.forEach { cookieManager.setCookie(url, it) }
        val httpUrl = url.toHttpUrl()
        val httpCookies = cookies.mapNotNull { Cookie.parse(httpUrl, it) }
        val uri = httpUrl.toUri()
        assertEquals(networkStorage.get(httpUrl), httpCookies)

        networkStorage.remove(uri)
        assertEquals(cookieManager.getCookie(url), "")
    }

    @Test fun `Test network CookieJar`() {
        val httpUrl = "https://httpbun.org/cookies/set/anitester/isterrible".toHttpUrl()
        network.client.newCall(GET(httpUrl)).execute()
        assert(network.client.cookieJar.loadForRequest(httpUrl).isNotEmpty())
    }
}
