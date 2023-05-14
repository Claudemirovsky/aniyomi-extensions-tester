/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.content.Context
import playwright.utils.PlaywrightStatics

abstract class WebSettings {
    abstract var loadWithOverviewMode: Boolean

    abstract var useWideViewPort: Boolean

    abstract var blockNetworkLoads: Boolean

    abstract var javaScriptEnabled: Boolean

    abstract var databaseEnabled: Boolean

    abstract var domStorageEnabled: Boolean

    abstract var userAgentString: String?

    abstract var cacheMode: Int

    companion object {
        @JvmStatic
        fun getDefaultUserAgent(context: Context) = PlaywrightStatics.userAgent

        const val LOAD_DEFAULT = -1

        @Deprecated("Obsolete, use LOAD_DEFAULT instead.")
        const val LOAD_NORMAL = 0
        const val LOAD_CACHE_ELSE_NETWORK = 1
        const val LOAD_NO_CACHE = 2
        const val LOAD_CACHE_ONLY = 3
    }
}
