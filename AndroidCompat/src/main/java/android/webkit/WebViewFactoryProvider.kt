package android.webkit

import com.microsoft.playwright.Browser
import com.microsoft.playwright.Page

interface WebViewFactoryProvider {
    fun loadUrl(url: String, additionalHeaders: Map<String, String>)

    fun loadUrl(url: String)

    fun loadData(data: String, mimeType: String?, encoding: String?)

    fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?,
    )

    fun addJavascriptInterface(obj: Any, name: String)

    fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?)

    val settings: WebSettings

    val browser: Browser

    val page: Page

    var webViewClient: WebViewClient
}
