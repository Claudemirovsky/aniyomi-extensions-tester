package tests.io

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.AfterAll
import suwayomi.tachidesk.utils.AnitesterUtils.loadPreferences
import tests.util.AnitesterTest
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.androidcompat.io.sharedprefs.JavaSharedPreferences
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SharedPrefsTests : AnitesterTest() {
    private val preferences by lazy { getPreference(javaClass.simpleName) }

    // to be deleted at the end of all tests
    private val preferencesList = mutableListOf<SharedPreferences>()

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

    @Test fun `Test loadPreferences utilitary function`() {
        val tempFile = File.createTempFile("SharedPrefsTests", ".txt.tmp")
        tempFile.deleteOnExit()
        val jsonData = """
            {
                "source_Dopebox/en": {
                    "str": "Some text",
                    "int": 69420,
                    "boolean": true,
                    "float": 3.141592,
                    "strset": ["i", "hate", "the", "antichrist"]
                }, 
                "source_8615824918772726940": {
                    "str": "Some text",
                    "int": 69420,
                    "boolean": true,
                    "float": 3.141592,
                    "strset": ["i", "hate", "the", "antichrist"]
                } 
            }
        """

        tempFile.writeText(jsonData)

        loadPreferences(tempFile.toPath())

        // Dopebox ID
        assertPreferences(getPreference("source_787491081765201367"))
        // Sflix ID
        assertPreferences(getPreference("source_8615824918772726940"))
    }

    private fun assertPreferences(prefs: SharedPreferences) {
        assertEquals(prefs.getString("str", ""), "Some text")
        assertEquals(prefs.getInt("int", 0), 69420)
        assertEquals(prefs.getBoolean("boolean", false), true)
        assertEquals(prefs.getFloat("float", 0.0F), 3.141592F)
        assertEquals(prefs.getStringSet("strset", setOf("")), setOf("i", "hate", "the", "antichrist"))
    }

    private fun getPreference(name: String): SharedPreferences {
        val pref = Injekt.get<Application>()
            .getSharedPreferences(name, Context.MODE_PRIVATE)
        preferencesList.add(pref)
        return pref
    }

    @AfterAll
    fun cleanup() {
        preferencesList.forEach { (it as JavaSharedPreferences).deleteAll() }
    }
}
