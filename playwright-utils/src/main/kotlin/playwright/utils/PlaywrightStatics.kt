package playwright.utils

import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Playwright
import mu.KotlinLogging
import java.nio.file.Paths

object PlaywrightStatics {
    var usedPlaywright = false
    val playwrightInstance by lazy {
        System.setProperty("playwright.driver.impl", "playwright.utils.CustomDriver")
        preinstalledChromium?.also {
            System.setProperty(CustomDriver.PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD, "1")
        }
        usedPlaywright = true
        Playwright.create()
    }

    private val preinstalledChromium by lazy {
        findBinary("chromium", "chromium.exe", "chromium-browser")
    }

    val launchOptions: LaunchOptions
        get() {
            return LaunchOptions().apply {
                headless = false
                chromiumSandbox = false
                executablePath = preinstalledChromium?.let(Paths::get)
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
            playwrightInstance.chromium().launch(launchOptions.setHeadless(true)).use { browser ->
                browser.newPage().use { page ->
                    val userAgent = (page.evaluate("() => {return navigator.userAgent}") as String)
                        .replace("Headless", "")

                    logger.debug { "WebView User-Agent is $userAgent" }
                    userAgent
                }
            }
        }.getOrDefault(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36",
        )
    }
}
