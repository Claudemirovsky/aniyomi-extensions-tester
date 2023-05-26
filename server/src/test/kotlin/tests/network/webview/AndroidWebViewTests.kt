package tests.network.webview

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import tests.util.AnitesterTest
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

        handler.post {
            webView = WebView(context)
            webView?.addJavascriptInterface(jsi, "android")
            webView?.loadUrl("https://www.google.com/")
            webView?.evaluateJavascript("window.android.passPayload(window.location.href)") {
                assert("google.com" in it) { "EvaluateJS Callback failed" }
                latch.countDown()
            }
        }

        val result = latch.await(10, TimeUnit.SECONDS)

        killWebView()
        assert("google.com" in jsi.passedData) { "JSI Failed at being exposed and called." }
        assert(result) { "A javascript-related function wasn't called." }
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