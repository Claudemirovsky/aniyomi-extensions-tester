package xyz.nulldev.androidcompat.androidimpl

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.HttpWebConnection
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.WebResponse
import com.gargoylesoftware.htmlunit.WebResponseData
import com.gargoylesoftware.htmlunit.util.NameValuePair
import java.net.URL

class FakeWebViewClient(
    private val client: WebClient,
    private val ctx: Context,
    private val webViewClient: WebViewClient? = null
) : WebViewClient() {

    private val localWebView by lazy { WebView(ctx) }
    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse {
        val res = client.webConnection.getResponse(FakeWebRequest(request))
        return FakeWebResourceResponse(res)
    }

    init {
        webViewClient?.also { web ->
            val newConnection = object : HttpWebConnection(client) {
                override fun getResponse(request: WebRequest): WebResponse {
                    return web.shouldInterceptRequest(
                        localWebView,
                        FakeWebResourceRequest(request)
                    )
                        ?.let { FakeWebResponse(it) }
                        ?: super.getResponse(request)
                }
            }
            client.webConnection = newConnection
        }
    }
}

class FakeWebResourceRequest(private val request: WebRequest) : WebResourceRequest {
    override fun getUrl() = Uri.parse(request.url.toString())

    override fun getRequestHeaders() = request.additionalHeaders

    override fun getMethod() = request.httpMethod.toString()

    override fun isRedirect() = false
    override fun isForMainFrame() = false
    override fun hasGesture() = false
}

class FakeWebResourceResponse(
    private val response: WebResponse
) : WebResourceResponse(
    response.contentType,
    response.contentAsString,
    response.statusCode,
    response.statusMessage,
    response.responseHeaders.map { it.name to it.value }.toMap(),
    response.contentAsStream
)

class FakeWebRequest(private val request: WebResourceRequest) :
    WebRequest(URL(request.url.toString())) {
    override fun getAdditionalHeaders() = request.requestHeaders
    override fun getHttpMethod() = HttpMethod.valueOf(request.method)
}

class FakeWebResponse(private val response: WebResourceResponse) :
    WebResponse(
        WebResponseData(
            response.data.readBytes(),
            response.statusCode,
            response.reasonPhrase,
            response.responseHeaders.map { (name, value) ->
                NameValuePair(name, value)
            }
        ),
        URL(""),
        HttpMethod.GET,
        0L
    )
