/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.webkit

open class WebViewClient {
    open fun onPageStarted(view: WebView, url: String) {}

    open fun onPageFinished(view: WebView, url: String) {}

    open fun onLoadResource(view: WebView, url: String) {}

    @Deprecated("Use shouldInterceptRequest(WebView, WebResourceRequest) instead.")
    open fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? = null

    open fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? = null

    @Deprecated("Use shouldOverrideUrlLoading(WebView, WebResourceRequest) instead")
    open fun shouldOverrideUrlLoading(view: WebView, url: String) = false

    open fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false
}
