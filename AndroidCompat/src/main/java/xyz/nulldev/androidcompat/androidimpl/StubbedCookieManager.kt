package xyz.nulldev.androidcompat.androidimpl

import android.app.Application
import android.content.Context
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

@Suppress("DEPRECATION", "EmptyFunctionBlock")
open class StubbedCookieManager : CookieManager() {
    companion object {
        val preferences by lazy {
            Injekt
                .get<Application>()
                .getSharedPreferences("cookie_store", Context.MODE_PRIVATE)
        }

        val cookieMap by lazy {
            ConcurrentHashMap<String, List<Cookie>>().apply {
                val groups = preferences.all.entries.groupBy { (key, _) -> key.substringBeforeLast(".") }
                for ((domain, items) in groups.entries) {
                    @Suppress("UNCHECKED_CAST")
                    val cookies = items.mapNotNull { it.value as? String }
                    val url = "http://$domain".toHttpUrlOrNull() ?: continue

                    runCatching {
                        val nonExpiredCookies = cookies.mapNotNull { Cookie.parse(url, it) }
                            .filter { !it.hasExpired() }
                        addOrReplace(this, domain, nonExpiredCookies)
                    }
                }
            }
        }

        @Synchronized
        private fun addOrReplace(
            list: ConcurrentHashMap<String, List<Cookie>>,
            domain: String,
            cookies: List<Cookie>,
        ): List<Cookie> {
            // Append or replace the cookies for this domain.
            val cookiesForDomain = list[domain].orEmpty().toMutableList()
            for (cookie in cookies) {
                // Find a cookie with the same name. Replace it if found, otherwise add a new one.
                val pos = cookiesForDomain.indexOfFirst { it.name == cookie.name }
                if (pos == -1) {
                    cookiesForDomain.add(cookie)
                } else {
                    cookiesForDomain[pos] = cookie
                }
            }
            list.put(domain, cookiesForDomain)
            return cookiesForDomain
        }

        @JvmStatic
        protected fun Cookie.hasExpired() = System.currentTimeMillis() >= expiresAt
    }

    override fun setAcceptCookie(accept: Boolean) {}

    override fun acceptCookie() = true

    override fun setAcceptThirdPartyCookies(webview: WebView?, accept: Boolean) {}

    override fun acceptThirdPartyCookies(webview: WebView?) = true

    override fun setCookie(url: String, value: String) {
        setCookie(URI(url), value)
    }

    @Synchronized
    fun addAll(url: HttpUrl, cookies: List<Cookie>) {
        val domain = url.toUri().host
        val cookiesForDomain = addOrReplace(cookieMap, domain, cookies)

        // Get cookies to be stored in disk
        val newValues = cookiesForDomain.asSequence()
            .filter { it.persistent && !it.hasExpired() }
            .map(Cookie::toString)
            .toSet()

        preferences.edit().putStringSet(domain, newValues).apply()
    }

    @Synchronized
    private fun setCookie(uri: URI, value: String) {
        val domain = uri.host ?: return
        val url = "http://$domain".toHttpUrlOrNull() ?: return
        val cookie = Cookie.parse(url, value) ?: return
        addAll(url, listOf(cookie))
    }

    @Suppress("UnsafeCallOnNullableType")
    override fun setCookie(url: String?, value: String?, callback: ValueCallback<Boolean>?) {
        runCatching {
            setCookie(url!!, value!!)
        }
    }

    override fun getCookie(url: String): String {
        val host = URI(url).host ?: return ""
        return cookieMap[host]
            .orEmpty()
            .joinToString("; ") { "${it.name}=${it.value}" }
    }

    override fun getCookie(url: String?, privateBrowsing: Boolean): String {
        return url?.let { runCatching { getCookie(it) }.getOrNull() }.orEmpty()
    }

    @Deprecated("Deprecated in java")
    override fun removeSessionCookie() {}

    override fun removeSessionCookies(callback: ValueCallback<Boolean>?) {}

    @Synchronized
    @Deprecated("Deprecated in java")
    override fun removeAllCookie() {
        preferences.edit().clear().apply()
        cookieMap.clear()
    }

    override fun removeAllCookies(callback: ValueCallback<Boolean>?) {
        removeAllCookie()
    }

    override fun hasCookies() = !cookieMap.isEmpty()

    override fun hasCookies(privateBrowsing: Boolean) = hasCookies()

    @Deprecated("Deprecated in java")
    override fun removeExpiredCookie() {}

    override fun flush() {}

    override fun allowFileSchemeCookiesImpl() = true

    override fun setAcceptFileSchemeCookiesImpl(accept: Boolean) {}
}
