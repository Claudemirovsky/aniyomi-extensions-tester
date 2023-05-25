package tests.io

import android.app.Application
import android.content.Context
import tests.util.AnitesterTest
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SharedPrefsTests : AnitesterTest() {
    private val preferences by lazy {
        Injekt
            .get<Application>()
            .getSharedPreferences(javaClass.simpleName, Context.MODE_PRIVATE)
    }

    private val key = "test_pref_key"

    @Test fun `Test putString method`() {
        val stringData = "some data"
        preferences.edit().putString(key, stringData).apply()
        assertEquals(preferences.getString(key, null), stringData)

        preferences.edit().putString(key, null).apply()
        assertNull(preferences.getString(key, null))
    }

    @Test fun `Test putStringSet method`() {
        val setData = setOf("a", "b", "c")
        preferences.edit().putStringSet(key, setData).apply()
        assertEquals(preferences.getStringSet(key, null), setData)
    }

    @Test fun `Test clear method`() {
        preferences.edit().putBoolean(key, false).apply()
        preferences.edit().clear().apply()
        assertNull(preferences.getString(key, null))
    }
}
