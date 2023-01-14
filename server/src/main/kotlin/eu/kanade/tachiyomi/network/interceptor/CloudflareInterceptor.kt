package eu.kanade.tachiyomi.network.interceptor

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.PlaywrightException
import com.microsoft.playwright.options.WaitForSelectorState
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.interceptor.CFClearance.resolveWithWebView
import eu.kanade.tachiyomi.network.interceptor.playwright.CustomDriver
import eu.kanade.tachiyomi.network.interceptor.playwright.findBinary
import mu.KotlinLogging
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import uy.kohesive.injekt.injectLazy
import java.io.IOException
import java.nio.file.Paths
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class CloudflareInterceptor : Interceptor {
    private val logger = KotlinLogging.logger {}

    private val network: NetworkHelper by injectLazy()

    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        logger.trace { "CloudflareInterceptor is being used." }

        val originalResponse = chain.proceed(chain.request())

        // Check if Cloudflare anti-bot is on
        if (!(originalResponse.code in ERROR_CODES && originalResponse.header("Server") in SERVER_CHECK)) {
            return originalResponse
        }

        logger.debug { "Cloudflare anti-bot is on, CloudflareInterceptor is kicking in..." }

        return try {
            originalResponse.close()
            network.cookies.remove(originalRequest.url.toUri())

            val request = resolveWithWebView(originalRequest)

            chain.proceed(request)
        } catch (e: Exception) {
            // Because OkHttp's enqueue only handles IOExceptions, wrap the exception so that
            // we don't crash the entire app
            throw IOException(e)
        }
    }

    companion object {
        private val ERROR_CODES = listOf(403, 503)
        private val SERVER_CHECK = arrayOf("cloudflare-nginx", "cloudflare")
        private val COOKIE_NAMES = listOf("cf_clearance")
    }
}

/*
 * This class is ported from https://github.com/vvanglro/cf-clearance
 * The original code is licensed under Apache 2.0
*/
object CFClearance {
    private val logger = KotlinLogging.logger {}
    private val network: NetworkHelper by injectLazy()
    private val defaultOptions by lazy { LaunchOptions().setHeadless(false) }

    init {
        // Fix the default DriverJar issue by using Tachidesk's implementation
        // ref: https://github.com/microsoft/playwright-java/issues/1138
        System.setProperty(
            "playwright.driver.impl",
            "eu.kanade.tachiyomi.network.interceptor.playwright.CustomDriver"
        )

        val preinstalledChromium = findBinary("chromium", "chromium.exe", "chromium-browser")
        if (preinstalledChromium != null) {
            defaultOptions.setExecutablePath(Paths.get(preinstalledChromium))
            System.setProperty(CustomDriver.PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD, "1")
        }
    }

    fun resolveWithWebView(originalRequest: Request): Request {
        val url = originalRequest.url.toString()

        logger.debug { "resolveWithWebView($url)" }

        val cookies = Playwright.create().use { playwright ->
            playwright.chromium().launch(defaultOptions).use { browser ->
                val userAgent = originalRequest.header("User-Agent")
                if (userAgent != null) {
                    browser.newContext(Browser.NewContextOptions().setUserAgent(userAgent)).use { browserContext ->
                        browserContext.newPage().use { getCookies(it, url) }
                    }
                } else {
                    browser.newPage().use { getCookies(it, url) }
                }
            }
        }

        // Copy cookies to cookie store
        cookies.map {
            network.cookies.addAll(
                url = HttpUrl.Builder()
                    .scheme("http")
                    .host(it.domain)
                    .build(),
                cookies = cookies
            )
        }
        // Merge new and existing cookies for this request
        // Find the cookies that we need to merge into this request
        val convertedForThisRequest = cookies.filter {
            it.matches(originalRequest.url)
        }
        // Extract cookies from current request
        val existingCookies = Cookie.parseAll(
            originalRequest.url,
            originalRequest.headers
        )
        // Filter out existing values of cookies that we are about to merge in
        val filteredExisting = existingCookies.filter { existing ->
            convertedForThisRequest.none { converted -> converted.name == existing.name }
        }
        logger.trace { "Existing cookies" }
        logger.trace { existingCookies.joinToString("; ") }
        val newCookies = filteredExisting + convertedForThisRequest
        logger.trace { "New cookies" }
        logger.trace { newCookies.joinToString("; ") }
        return originalRequest.newBuilder()
            .header("Cookie", newCookies.joinToString("; ") { "${it.name}=${it.value}" })
            .build()
    }

    fun getWebViewUserAgent(): String {
        return try {
            Playwright.create().use { playwright ->
                playwright.chromium().launch(defaultOptions).use { browser ->
                    browser.newPage().use { page ->
                        val userAgent = page.evaluate("() => {return navigator.userAgent}") as String
                        logger.debug { "WebView User-Agent is $userAgent" }
                        // prevents opening chromium again just to get the user-agent.
                        System.setProperty("http.agent", userAgent)
                        return userAgent
                    }
                }
            }
        } catch (e: PlaywrightException) {
            // Playwright might fail on headless environments like docker/github CI
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"
        }
    }

    private fun getCookies(page: Page, url: String): List<Cookie> {
        applyStealthInitScripts(page)
        page.navigate(url)
        val challengeResolved = waitForChallengeResolve(page)

        return if (challengeResolved) {
            val cookies = page.context().cookies()

            logger.debug {
                val userAgent = page.evaluate("() => {return navigator.userAgent}")
                "Playwright User-Agent is $userAgent"
            }

            // Convert PlayWright cookies to OkHttp cookies
            cookies.map {
                Cookie.Builder()
                    .domain(it.domain.removePrefix("."))
                    .expiresAt(it.expires?.times(1000)?.toLong() ?: Long.MAX_VALUE)
                    .name(it.name)
                    .path(it.path)
                    .value(it.value).apply {
                        if (it.httpOnly) httpOnly()
                        if (it.secure) secure()
                    }.build()
            }
        } else {
            logger.debug { "Cloudflare challenge failed to resolve" }
            throw CloudflareBypassException()
        }
    }

    // ref: https://github.com/vvanglro/cf-clearance/blob/44124a8f06d8d0ecf2bf558a027082ff88dab435/cf_clearance/stealth.py#L18
    private val stealthInitScripts by lazy {
        arrayOf(
            "/cloudflare-js/canvas.fingerprinting.js",
            "/cloudflare-js/chrome.global.js",
            "/cloudflare-js/emulate.touch.js",
            "/cloudflare-js/navigator.permissions.js",
            "/cloudflare-js/navigator.webdriver.js",
            "/cloudflare-js/chrome.runtime.js",
            "/cloudflare-js/chrome.plugin.js"
        ).map { javaClass.getResource(it)!!.readText() }
    }

// ref: https://github.com/vvanglro/cf-clearance/blob/44124a8f06d8d0ecf2bf558a027082ff88dab435/cf_clearance/stealth.py#L76
    private fun applyStealthInitScripts(page: Page) {
        for (script in stealthInitScripts) {
            page.addInitScript(script)
        }
    }

    // ref: https://github.com/vvanglro/cf-clearance/blob/44124a8f06d8d0ecf2bf558a027082ff88dab435/cf_clearance/retry.py#L21
    private fun waitForChallengeResolve(page: Page): Boolean {
        // sometimes the user has to solve the captcha challenge manually, potentially wait a long time
        val timeoutSeconds = 120.seconds.toDouble(DurationUnit.MILLISECONDS)
        return runCatching {
            page.waitForSelector(
                "#challenge-form",
                Page.WaitForSelectorOptions().apply {
                    timeout = timeoutSeconds
                    state = WaitForSelectorState.DETACHED
                }
            )
            true
        }.getOrDefault(false)
    }

    private class CloudflareBypassException : Exception()
}
