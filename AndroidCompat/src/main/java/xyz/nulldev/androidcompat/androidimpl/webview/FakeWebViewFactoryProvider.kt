package xyz.nulldev.androidcompat.androidimpl.webview

import android.content.Context
import android.webkit.WebViewClient
import android.webkit.WebViewFactoryProvider
import android.webkit.WebViewFactoryProvider.Statics
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlPage
import java.net.URL

class FakeWebViewFactoryProvider(private val ctx: Context) : WebViewFactoryProvider {

    override val webclient by lazy {
        WebClient(BrowserVersion.BEST_SUPPORTED).apply {
            cookieManager = HtmlUnitCookieManager()
            options.apply {
                isThrowExceptionOnFailingStatusCode = false
                isPrintContentOnFailingStatusCode = false
                isThrowExceptionOnScriptError = false
                isJavaScriptEnabled = true
                isCssEnabled = true
                setUseInsecureSSL(true)
            }
        }
    }

    override var webViewClient: WebViewClient
        get() = FakeWebViewClient(webclient, ctx)
        set(value) { FakeWebViewClient(webclient, ctx, value) }

    class FakeStatics : Statics {
        override fun getDefaultUserAgent(context: Context): String =
            BrowserVersion.BEST_SUPPORTED.userAgent
    }

    override fun getStatics(): Statics = FakeStatics()

    override fun loadUrl(url: String, additionalHeaders: Map<String, String>) {
        val request = WebRequest(URL(url)).apply {
            setAdditionalHeaders(additionalHeaders)
        }
        webclient.getPage<HtmlPage>(request)
    }

    override fun loadUrl(url: String) { webclient.getPage<HtmlPage>(url) }

    override val settings = FakeWebViewSettings(webclient.options)
}
