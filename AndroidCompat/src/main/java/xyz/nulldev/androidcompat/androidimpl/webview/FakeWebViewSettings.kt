package xyz.nulldev.androidcompat.androidimpl.webview

import android.webkit.WebSettings
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClientOptions

class FakeWebViewSettings(private val options: WebClientOptions) : WebSettings() {
    override var useWideViewPort: Boolean = false
    override var loadWithOverviewMode: Boolean = false
    override var databaseEnabled: Boolean = false
    override var domStorageEnabled: Boolean = false
    override var blockNetworkLoads: Boolean = false
    override var userAgentString: String? = BrowserVersion.BEST_SUPPORTED.userAgent
    override var javaScriptEnabled: Boolean
        get() = options.isJavaScriptEnabled
        set(value) {
            options.setJavaScriptEnabled(value)
        }
}
