package tests.network.webview

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.util.asJsoup
import tests.util.AnitesterTest
import uy.kohesive.injekt.injectLazy
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertNotNull

class CloudflareTests : AnitesterTest() {
    private val network: NetworkHelper by injectLazy()

    @Test fun `Is passing Cloudflare IUAM protection`() {
        network.cookieManager.store.remove(URI(URL))
        val request = GET(URL)
        val response = network.cloudflareClient.newCall(request).execute()
        val doc = response.use { it.asJsoup() }
        assertNotNull(doc.selectFirst(".hystericalbg"))
    }

    companion object {
        private const val URL = "https://nowsecure.nl/#relax"
    }
}
