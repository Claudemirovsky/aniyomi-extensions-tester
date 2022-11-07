package xyz.nulldev.androidcompat.androidimpl

import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView

class StubbedCookieManager : CookieManager() {
    override fun setAcceptCookie(accept: Boolean) {
        throw NotImplementedError()
    }

    override fun acceptCookie(): Boolean {
        throw NotImplementedError()
    }

    override fun setAcceptThirdPartyCookies(webview: WebView?, accept: Boolean) {
    }

    override fun acceptThirdPartyCookies(webview: WebView?): Boolean {
        throw NotImplementedError()
    }

    override fun setCookie(url: String, value: String) {
    }

    override fun setCookie(url: String?, value: String?, callback: ValueCallback<Boolean>?) {
    }

    override fun getCookie(url: String?): String {
        throw NotImplementedError()
    }

    override fun getCookie(url: String?, privateBrowsing: Boolean): String {
        throw NotImplementedError()
    }

    override fun removeSessionCookie() {
    }

    override fun removeSessionCookies(callback: ValueCallback<Boolean>?) {
    }

    override fun removeAllCookie() {
    }

    override fun removeAllCookies(callback: ValueCallback<Boolean>?) {
    }

    override fun hasCookies(): Boolean {
        throw NotImplementedError()
    }

    override fun hasCookies(privateBrowsing: Boolean): Boolean {
        throw NotImplementedError()
    }

    override fun removeExpiredCookie() {
    }

    override fun flush() {
    }

    override fun allowFileSchemeCookiesImpl(): Boolean {
        throw NotImplementedError()
    }

    override fun setAcceptFileSchemeCookiesImpl(accept: Boolean) {
    }
}