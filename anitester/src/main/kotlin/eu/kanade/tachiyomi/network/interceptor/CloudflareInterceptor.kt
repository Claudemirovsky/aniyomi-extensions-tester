package eu.kanade.tachiyomi.network.interceptor

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import com.microsoft.playwright.Browser
import com.microsoft.playwright.Page
import com.microsoft.playwright.Route
import com.microsoft.playwright.options.WaitForSelectorState
import eu.kanade.tachiyomi.network.PersistentCookieStore
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import playwright.utils.PlaywrightStatics
import playwright.utils.PlaywrightStatics.browser
import java.io.IOException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class CloudflareInterceptor(
    private val cookieJar: PersistentCookieStore,
) : Interceptor {
    private val logger = KotlinLogging.logger {}
    private val cfClearance by lazy { CFClearance(cookieJar) }

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
            cookieJar.remove(originalRequest.url.toUri())

            val request = cfClearance.resolveWithWebView(originalRequest)

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
    }
}

/*
 * This class is ported from https://github.com/vvanglro/cf-clearance
 * The original code is licensed under Apache 2.0
*/
class CFClearance(
    private val cookieJar: PersistentCookieStore,
) {
    private val logger = KotlinLogging.logger {}

    private val defaultOptions by lazy { PlaywrightStatics.launchOptions }

    fun resolveWithWebView(originalRequest: Request): Request {
        val url = originalRequest.url.toString()

        logger.debug { "resolveWithWebView($url)" }

        val cookies = PlaywrightStatics.playwrightInstance.let { playwright ->
            playwright.browser().launch(defaultOptions).use { browser ->
                val ctxOptions = Browser.NewContextOptions().apply {
                    originalRequest.header("User-Agent")
                        ?.let(::setUserAgent)
                }

                browser.newContext(ctxOptions).use { browserContext ->
                    browserContext.newPage().use { getCookies(it, url) }
                }
            }
        }

        // Copy cookies to cookie store
        cookies.map {
            cookieJar.addAll(
                url = HttpUrl.Builder()
                    .scheme("http")
                    .host(it.domain)
                    .build(),
                cookies = cookies,
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
            originalRequest.headers,
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

    private fun getCookies(page: Page, url: String): List<Cookie> {
        applyStealthInitScripts(page)
        page.route("**") { route ->
            val resp = route.fetch()
            // Fix https://github.com/microsoft/playwright/issues/21780
            val headers = resp.headers().apply {
                remove("cross-origin-embedder-policy")
                remove("cross-origin-opener-policy")
                remove("cross-origin-resource-policy")
                remove("origin-agent-cluster")
                remove("referrer-policy")
                remove("x-frame-options")
                remove("sec-fetch-site")
                remove("sec-fetch-mode")
                remove("sec-fetch-dest")
                remove("sec-fetch-user")
                remove("upgrade-insecure-requests")
            }

            route.fulfill(Route.FulfillOptions().setResponse(resp).setHeaders(headers))
        }
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
                    .expiresAt(it.expires?.run { times(1000).toLong() } ?: Long.MAX_VALUE)
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
            "/cloudflare-js/chrome.plugin.js",
        ).map { javaClass.getResource(it)!!.readText() }
    }

    // ref: https://github.com/vvanglro/cf-clearance/blob/44124a8f06d8d0ecf2bf558a027082ff88dab435/cf_clearance/stealth.py#L76
    private fun applyStealthInitScripts(page: Page) {
        stealthInitScripts.forEach(page::addInitScript)
    }

    // ref: https://github.com/vvanglro/cf-clearance/blob/44124a8f06d8d0ecf2bf558a027082ff88dab435/cf_clearance/retry.py#L21
    private fun waitForChallengeResolve(page: Page): Boolean {
        // sometimes the user has to solve the captcha challenge manually, potentially wait a long time
        val waitOptions = Page.WaitForSelectorOptions().apply {
            timeout = 5.seconds.toDouble(DurationUnit.MILLISECONDS)
            state = WaitForSelectorState.DETACHED
        }

        val query = ".main-content > #challenge-stage"
        val turnstileTexts = setOf("turnstile", "challenge")
        var success = false

        repeat(10) {
            success = runCatching { page.querySelector(query) }.getOrNull() == null

            if (success) return success

            runCatching {
                // Turnstile challenge
                page.mainFrame().childFrames().forEach { frame ->
                    val url = frame.url()
                    if (turnstileTexts.all(url::contains)) {
                        frame.querySelector("label.ctp-checkbox-label")
                            ?.querySelector("area, input")
                            ?.also { elem ->
                                val box = elem.boundingBox()
                                page.mouse().click(box.x + box.width / 2, box.y + box.height / 2)
                            }
                    }
                }

                page.waitForSelector(query, waitOptions)
                success = true
            }
        }

        return success
    }

    private class CloudflareBypassException : Exception()
}
