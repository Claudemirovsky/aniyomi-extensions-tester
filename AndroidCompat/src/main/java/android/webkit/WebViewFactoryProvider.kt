package android.webkit

import android.content.Context
import com.gargoylesoftware.htmlunit.WebClient

interface WebViewFactoryProvider {
    interface Statics {
        fun getDefaultUserAgent(context: Context): String
    }

    fun getStatics(): Statics

    fun loadUrl(url: String, additionalHeaders: Map<String, String>)
    fun loadUrl(url: String)

    val settings: WebSettings

    var webViewClient: WebViewClient

    val webclient: WebClient
}
