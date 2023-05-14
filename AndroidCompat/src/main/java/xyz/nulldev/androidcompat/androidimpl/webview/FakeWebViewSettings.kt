package xyz.nulldev.androidcompat.androidimpl.webview

import android.webkit.WebSettings

class FakeWebViewSettings : WebSettings() {
    override var useWideViewPort: Boolean = false
    override var loadWithOverviewMode: Boolean = false
    override var databaseEnabled: Boolean = false
    override var domStorageEnabled: Boolean = false
    override var blockNetworkLoads: Boolean = false
    override var userAgentString: String? = null
    override var javaScriptEnabled: Boolean = true
    override var cacheMode: Int = WebSettings.LOAD_DEFAULT
}
