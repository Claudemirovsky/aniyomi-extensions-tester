package tests.network.webview

import anitester.AnitesterTest
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.util.asJsoup
import uy.kohesive.injekt.injectLazy
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertNotNull

class CloudflareTests : AnitesterTest() {
    private val network: NetworkHelper by injectLazy()

    @Test fun `Is passing Cloudflare IUAM protection`() {
        network.cookieManager.store.remove(URI(URL))
        val request = GET(URL)
        val response = network.client.newCall(request).execute()
        val doc = response.asJsoup()
        assertNotNull(doc.selectFirst("div.index-popular div.gallery"))
    }

    companion object {
        private const val URL = "https://nhentai.net/"
    }
}
