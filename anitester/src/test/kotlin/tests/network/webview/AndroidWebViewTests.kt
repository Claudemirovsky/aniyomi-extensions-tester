package tests.network.webview

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import anitester.AnitesterTest
import uy.kohesive.injekt.injectLazy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test

class AndroidWebViewTests : AnitesterTest() {
    private val context: Application by injectLazy()
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var webView: WebView? = null

    @Test fun `Test WebViewClient functions`() {
        val latch = CountDownLatch(5)

        handler.post {
            webView = WebView(context)
            webView?.webViewClient = object : WebViewClient() {
                val pageStarted by lazy { latch.countDown() }
                override fun onPageStarted(view: WebView, url: String) {
                    pageStarted
                }

                val pageFinished by lazy { latch.countDown() }
                override fun onPageFinished(view: WebView, url: String) {
                    pageFinished
                }

                val loadResource by lazy { latch.countDown() }
                override fun onLoadResource(view: WebView, url: String) {
                    loadResource
                }

                val interceptRequest by lazy { latch.countDown() }
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    interceptRequest
                    return super.shouldInterceptRequest(view, request)
                }

                val urlLoading by lazy { latch.countDown() }
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    urlLoading
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }

            webView?.loadUrl("https://google.com")
        }

        val result = latch.await(10, TimeUnit.SECONDS)
        killWebView()

        assert(result) { "Not all WebView functions were called successfully." }
    }

    @Test fun `Test exposed JavascriptInterface`() {
        val latch = CountDownLatch(2)

        class JSInterface(val latch: CountDownLatch) {
            var passedData = ""

            @JavascriptInterface
            fun passPayload(payload: String): String {
                passedData = payload
                latch.countDown()
                return payload
            }
        }

        val jsi = JSInterface(latch)
        val url = "about:blank"

        handler.post {
            webView = WebView(context)
            webView?.addJavascriptInterface(jsi, "android")
            webView?.loadData("", null, null)
            webView?.evaluateJavascript("window.android.passPayload(window.location.href)") {
                assert(url in it) { "EvaluateJS Callback failed" }
                latch.countDown()
            }
        }

        val result = latch.await(10, TimeUnit.SECONDS)

        killWebView()
        assert(url in jsi.passedData) { "JSI Failed at being exposed and called." }
        assert(result) { "A javascript-related function wasn't called." }
    }

    // TODO: Use something like MockServer instead of relying on a third-party service.
    @Test fun `Test WebView CookieManager`() {
        val latch = CountDownLatch(1)
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies() {}

        val cookieUrl = "https://httpbun.org/cookies/set/anitester/isterrible"
        handler.post {
            webView = WebView(context)
            webView?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    latch.countDown()
                }
            }
            webView?.loadUrl(cookieUrl)
        }

        latch.await(5, TimeUnit.SECONDS)
        killWebView()

        assert(cookieManager.getCookie(cookieUrl).isNotEmpty())
    }

    fun killWebView() {
        val latch = CountDownLatch(1)
        handler.post {
            webView?.stopLoading()
            webView?.destroy()
            webView = null
            latch.countDown()
        }
        latch.await()
    }
}
