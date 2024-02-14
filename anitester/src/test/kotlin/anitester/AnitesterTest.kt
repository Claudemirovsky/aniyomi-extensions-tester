package anitester

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AnitesterTest {
    companion object {
        private var isInitialized = false

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            if (!isInitialized) {
                isInitialized = true
                initApplication()
            }
        }
    }
}
