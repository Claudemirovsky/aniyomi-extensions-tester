package tests.util

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import suwayomi.tachidesk.server.applicationSetup
import uy.kohesive.injekt.api.get

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AnitesterTest {
    companion object {
        private var isInitialized = false

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            if (!isInitialized) {
                isInitialized = true
                applicationSetup()
            }
        }
    }
}
