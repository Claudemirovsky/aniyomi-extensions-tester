package tests.webview

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.util.asJsoup
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import suwayomi.tachidesk.server.applicationSetup
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.test.Test
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CloudflareTests {
    private val network by lazy { Injekt.get<NetworkHelper>() }

    @BeforeAll
    fun startServer() = applicationSetup()

    @Test fun passCloudflareIUAM() {
        val request = GET("https://nowsecure.nl/#relax")
        val response = network.cloudflareClient.newCall(request).execute()
        val doc = response.use { it.asJsoup() }
        assertNotNull(doc.selectFirst(".hystericalbg"))
    }
}
