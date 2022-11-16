package android.webkit

import android.annotation.SystemApi
import android.content.Context
import xyz.nulldev.androidcompat.androidimpl.FakeWebViewFactoryProvider

@SystemApi
class WebViewFactory {
    companion object {
        @JvmStatic
        fun getProvider(ctx: Context): WebViewFactoryProvider =
            FakeWebViewFactoryProvider(ctx)
    }
}
