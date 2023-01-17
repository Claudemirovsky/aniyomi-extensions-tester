package xyz.nulldev.androidcompat.androidimpl

import android.content.Context
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import java.net.HttpCookie
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.prefs.Preferences

@Suppress("DEPRECATION")
class StubbedCookieManager : CookieManager() {
    private val COOKIE_KEY = "cookie_store"

    val prefs by lazy {
        val ctx = CustomContext()
        ctx.getSharedPreferences(COOKIE_KEY, Context.MODE_PRIVATE)
    }

    val cookieMap = ConcurrentHashMap<String, List<HttpCookie>>()

    init {
        for ((key, value) in prefs.all) {
            val realKey = key.substringBeforeLast(".")
            @Suppress("UNCHECKED_CAST")
            val cookieString = value as? String
            if (cookieString != null) {
                runCatching {
                    val cookie = HttpCookie.parse(cookieString).first()
                    addOrUpdate(realKey, cookie)
                }
            }
        }
    }

    override fun setAcceptCookie(accept: Boolean) {}

    override fun acceptCookie() = true

    override fun setAcceptThirdPartyCookies(webview: WebView?, accept: Boolean) {}

    override fun acceptThirdPartyCookies(webview: WebView?) = true

    override fun setCookie(url: String, value: String) {
        setCookie(URI(url), value)
    }

    private fun addOrUpdate(key: String, cookie: HttpCookie): MutableList<HttpCookie> {
        // Append or replace the cookies for this domain.
        val cookiesForDomain = cookieMap[key].orEmpty().toMutableList()
        // Find a cookie with the same name.
        // Replace it if found, otherwise add a new one.
        val pos = cookiesForDomain.indexOfFirst { it.name == cookie.name }
        if (pos == -1) {
            cookiesForDomain.add(cookie)
        } else {
            cookiesForDomain[pos] = cookie
        }

        cookieMap.put(key, cookiesForDomain)
        return cookiesForDomain
    }
    @Synchronized
    fun setCookie(uri: URI, value: String) {
        val key = uri.host ?: return
        val cookie = HttpCookie.parse(value).first()
        val allCookies = addOrUpdate(key, cookie)
        // Get cookies to be stored in disk
        val newValues = allCookies.asSequence()
            .filter { !it.hasExpired() }
            .map(HttpCookie::toString)
            .toSet()

        prefs.edit().putStringSet(key, newValues).apply()
    }

    override fun setCookie(url: String?, value: String?, callback: ValueCallback<Boolean>?) {
        runCatching {
            setCookie(url!!, value!!)
        }
    }

    override fun getCookie(url: String): String {
        val host = URI(url).host!!
        val cookies = cookieMap[host]
            .orEmpty()
            .filter { !it.hasExpired() }
            .joinToString(";")
        return cookies
    }

    override fun getCookie(url: String?, privateBrowsing: Boolean): String {
        return runCatching { getCookie(url!!) }.getOrDefault("")
    }

    override fun removeSessionCookie() {}

    override fun removeSessionCookies(callback: ValueCallback<Boolean>?) {}

    @Synchronized
    override fun removeAllCookie() {
        prefs.edit().clear().apply()
        cookieMap.clear()
        Preferences.userRoot().node("suwayomi/tachidesk/$COOKIE_KEY").removeNode()
    }

    override fun removeAllCookies(callback: ValueCallback<Boolean>?) {
        removeAllCookie()
    }

    override fun hasCookies() = !cookieMap.isEmpty()

    override fun hasCookies(privateBrowsing: Boolean) = hasCookies()

    override fun removeExpiredCookie() {}

    override fun flush() {}

    override fun allowFileSchemeCookiesImpl() = true

    override fun setAcceptFileSchemeCookiesImpl(accept: Boolean) {}
}
