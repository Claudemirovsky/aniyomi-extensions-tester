package xyz.nulldev.androidcompat.androidimpl.webview

import com.gargoylesoftware.htmlunit.CookieManager
import com.gargoylesoftware.htmlunit.util.Cookie
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import java.util.Date

class HtmlUnitCookieManager : CookieManager() {
    private val cookieManager = StubbedCookieManager()
    private val cookies_: MutableSet<Cookie> by lazy {
        cookieManager.cookieMap.flatMap { (host, cookies) ->
            cookies.map { cookie ->
                Cookie(host, cookie.name, cookie.value)
            }
        }.toMutableSet()
    }

    override fun clearExpired(date: Date) = false

    @Synchronized
    override fun addCookie(cookie: Cookie) {
        cookieManager.setCookie(cookie.domain, cookie.toString())
        super.addCookie(cookie)
    }

    @Synchronized
    override fun clearCookies() {
        cookies_.clear()
        cookieManager.removeAllCookie()
    }
}
