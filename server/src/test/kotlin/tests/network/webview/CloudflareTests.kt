package tests.network.webview

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.util.asJsoup
import tests.util.AnitesterTest
import uy.kohesive.injekt.injectLazy
import kotlin.test.Test
import kotlin.test.assertNotNull

class CloudflareTests : AnitesterTest() {
    private val network: NetworkHelper by injectLazy()

    @Test fun `Is passing Cloudflare IUAM protection`() {
        val request = GET("https://nowsecure.nl/#relax")
        val response = network.cloudflareClient.newCall(request).execute()
        val doc = response.use { it.asJsoup() }
        assertNotNull(doc.selectFirst(".hystericalbg"))
    }
}
