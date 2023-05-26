package android.webkit

import android.content.Context
import xyz.nulldev.androidcompat.androidimpl.webview.FakeWebViewFactoryProvider

class WebView(private val context: Context) {
    private val mProvider by lazy { FakeWebViewFactoryProvider(this) }

    var webViewClient: WebViewClient
        get() = mProvider.webViewClient
        set(value) { mProvider.webViewClient = value }

    val settings: WebSettings = mProvider.settings

    @Suppress("UNUSED_PARAMETER")
    fun clearCache(includeDiskFiles: Boolean) {}

    fun clearFormData() {}
    fun clearHistory() {}

    fun stopLoading() = destroy()

    fun destroy() = mProvider.destroy()

    fun loadUrl(url: String, additionalHeaders: Map<String, String>) =
        mProvider.loadUrl(url, additionalHeaders)

    fun loadUrl(url: String) = mProvider.loadUrl(url)

    fun loadData(data: String, mimeType: String?, encoding: String?) {
        mProvider.loadData(data, mimeType, encoding)
    }

    fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?,
    ) {
        mProvider.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    fun addJavascriptInterface(obj: Any, name: String) {
        mProvider.addJavascriptInterface(obj, name)
    }

    fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?) {
        mProvider.evaluateJavascript(script, resultCallback)
    }
}
