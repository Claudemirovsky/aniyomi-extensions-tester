package xyz.nulldev.androidcompat.androidimpl.webview

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebViewFactoryProvider
import com.microsoft.playwright.Browser.NewPageOptions
import com.microsoft.playwright.Request
import com.microsoft.playwright.Route
import com.microsoft.playwright.options.ServiceWorkerPolicy
import playwright.utils.PlaywrightStatics
import kotlin.reflect.full.declaredMemberFunctions

class FakeWebViewFactoryProvider(private val view: WebView) : WebViewFactoryProvider {

    override var webViewClient = WebViewClient()

    override fun loadUrl(url: String, additionalHeaders: Map<String, String>) {
        page.setExtraHTTPHeaders(additionalHeaders)
        loadUrl(url)
    }

    override fun loadUrl(url: String) { runCatching { page.navigate(url) } }

    override fun loadData(data: String, mimeType: String?, encoding: String?) {
        runCatching { page.setContent(data) }
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?,
    ) {
        baseUrl?.let(pageOptions::setBaseURL)
        loadData(data, mimeType, encoding)
    }

    private val functionsMap by lazy { mutableMapOf<String, String>() }

    override fun addJavascriptInterface(obj: Any, name: String) {
        runCatching {
            obj::class.declaredMemberFunctions.forEach { function ->
                val exportedFunction = "${name}_${function.name}"
                val realFunction = "$name.${function.name}"
                functionsMap[realFunction] = exportedFunction

                page.exposeFunction(exportedFunction) { args ->
                    val result = function.call(obj, *args)
                    when {
                        result is Unit -> ""
                        else -> result
                    }
                }
            }
        }
    }

    override fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?) {
        runCatching {
            val newScript = functionsMap.entries.fold(script) { code, entry ->
                code.replace(entry.key, entry.value)
            }
            page.evaluate(newScript)?.let { result ->
                resultCallback?.let {
                    it.onReceiveValue(result.toString()) 
                }
            }
        }
    }

    override val settings by lazy { FakeWebViewSettings() }

    private val pageOptions by lazy {
        NewPageOptions().apply {
            hasTouch = true
            isMobile = true
            javaScriptEnabled = settings.javaScriptEnabled
            serviceWorkers = ServiceWorkerPolicy.BLOCK
            userAgent = settings.userAgentString
        }
    }

    private val browserOptions by lazy {
        PlaywrightStatics.launchOptions.apply {
            if (!settings.databaseEnabled) args = args + listOf("--disable-databases")
            if (!settings.domStorageEnabled) args = args + listOf("--disable-local-storage")
        }
    }

    override val browser by lazy {
        PlaywrightStatics.playwrightInstance.chromium().launch(browserOptions)
    }

    override val page by lazy {
        browser.newPage(pageOptions).also {
            it.onDOMContentLoaded { pageObj ->
                webViewClient.onPageStarted(view, pageObj.url())
            }
            it.onLoad { pageObj ->
                webViewClient.onPageFinished(view, pageObj.url())
            }
            it.onRequest { request ->
                webViewClient.onLoadResource(view, request.url())
            }
            it.route("**") { route ->
                runCatching {
                    val request = route.request().toWebResourceRequest()
                    if (webViewClient.shouldOverrideUrlLoading(view, request)) {
                        route.abort()
                        true
                    } else {
                        webViewClient.shouldInterceptRequest(view, request)
                            ?.also { resource ->
                                route.fulfill(
                                    Route.FulfillOptions().apply {
                                        bodyBytes = resource.data.use { it.readBytes() }
                                        contentType = resource.mimeType
                                        headers = resource.responseHeaders
                                        status = resource.statusCode
                                    },
                                )
                            }
                    }
                }.getOrNull() ?: route.resume()
            }
        }
    }

    private fun Request.toWebResourceRequest(): WebResourceRequest = object : WebResourceRequest {
        override fun getUrl() = Uri.parse(this@toWebResourceRequest.url())

        override fun getRequestHeaders() = allHeaders()

        override fun getMethod() = this@toWebResourceRequest.method()

        override fun isRedirect() = redirectedFrom() == null
        override fun isForMainFrame() = isNavigationRequest()
        override fun hasGesture() = false
    }
}
