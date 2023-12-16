package playwright.utils

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Playwright
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Paths

object PlaywrightStatics {
    var usedPlaywright = false
    var useChromium = false

    val playwrightInstance: Playwright by lazy {
        System.setProperty("playwright.driver.impl", "playwright.utils.CustomDriver")
        preinstalledBrowser?.also {
            System.setProperty(CustomDriver.PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD, "1")
        }
        usedPlaywright = true
        Playwright.create()
    }

    fun Playwright.browser(): BrowserType = when {
        useChromium -> chromium()
        else -> firefox()
    }

    private val preinstalledBrowser by lazy {
        when {
            useChromium -> findBinary("chromium", "chromium.exe", "chromium-browser")
            else -> null // Playwright needs a patched firefox
        }
    }

    val launchOptions: LaunchOptions
        get() {
            return LaunchOptions().apply {
                headless = false
                chromiumSandbox = false
                executablePath = preinstalledBrowser?.let(Paths::get)
                args = listOf(
                    "--disable-gpu",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--no-first-run",
                    "--no-service-autorun",
                    "--no-default-browser-check",
                    "--password-store=basic",
                    "--incognito",
                )
            }
        }

    val userAgent by lazy {
        val logger = KotlinLogging.logger {}
        runCatching {
            playwrightInstance.browser().launch(launchOptions.setHeadless(true)).use { browser ->
                browser.newPage().use { page ->
                    val userAgent = (page.evaluate("() => {return navigator.userAgent}") as String)
                        .replace("Headless", "")

                    logger.debug { "WebView User-Agent is $userAgent" }
                    userAgent
                }
            }
        }.getOrElse {
            when {
                useChromium -> CHROMIUM_UA
                else -> FIREFOX_UA
            }
        }
    }
}

private const val CHROMIUM_UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36" +
    "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

private const val FIREFOX_UA = "Mozilla/5.0 (Windows NT 10.0; rv:120.0) Gecko/20100101 Firefox/120.0"
