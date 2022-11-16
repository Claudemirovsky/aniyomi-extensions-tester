package android.webkit

import android.content.Context

class WebView(private val context: Context) {
    private val mProvider by lazy { WebViewFactory.getProvider(context) }

    var webViewClient: WebViewClient
        get() = mProvider.webViewClient
        set(value) { mProvider.webViewClient = value }

    val settings = mProvider.settings

    fun clearHistory() {}

    fun stopLoading() {}

    fun destroy() {}

    fun loadUrl(url: String, additionalHeaders: Map<String, String>) =
        mProvider.loadUrl(url, additionalHeaders)

    fun loadUrl(url: String) = mProvider.loadUrl(url)
}
