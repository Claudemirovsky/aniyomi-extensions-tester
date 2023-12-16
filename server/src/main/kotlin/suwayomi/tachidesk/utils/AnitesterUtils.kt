package suwayomi.tachidesk.utils

import android.app.Application
import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.notExists
import kotlin.io.path.readLines
import kotlin.io.path.readText

object AnitesterUtils {
    private const val HTTP_ONLY_STR = "#HttpOnly_"

    private fun cookieFromLine(line: String): Cookie? {
        return when {
            line.startsWith(HTTP_ONLY_STR) || !line.startsWith("#") -> {
                val params = line.split("\t")
                if (params.size < 7) return null
                runCatching {
                    Cookie.Builder().apply {
                        val host = params.first().run {
                            if (startsWith(HTTP_ONLY_STR)) httpOnly()
                            substringAfter(HTTP_ONLY_STR).trim('.')
                        }

                        domain(host)
                        if (params.get(1).toBoolean()) {
                            hostOnlyDomain(host)
                        }

                        path(params.get(2))

                        if (params.get(3).toBoolean()) secure()

                        expiresAt(params.get(4).toLongOrNull()?.times(1000L) ?: 0L)
                        name(params.get(5))
                        value(params.get(6))
                    }.build()
                }.getOrNull()
            }
            else -> null
        }
    }

    fun loadCookies(file: Path) {
        if (file.notExists()) return

        val cookieMap = file.readLines()
            .mapNotNull(::cookieFromLine)
            .groupBy { it.domain }

        val manager = StubbedCookieManager()

        cookieMap.entries.forEach { (domain, cookies) ->
            manager.addAll("https://$domain".toHttpUrl(), cookies)
        }
    }

    fun loadPreferences(file: Path) {
        if (file.notExists()) return
        val items = runCatching {
            Json.decodeFromString<Map<String, Map<String, JsonElement>>>(file.readText())
        }.onFailure { it.printStackTrace() }.getOrNull() ?: return

        val prefix = "source_"
        val context = Injekt.get<Application>()
        items.entries.forEach { (filename, items) ->
            val realFilename = filename.run {
                when {
                    startsWith(prefix) && contains("/") ->
                        prefix + generateId(removePrefix(prefix))
                    else -> this
                }
            }

            val prefs = context.getSharedPreferences(realFilename, Context.MODE_PRIVATE).edit()
            runCatching {
                items.entries.forEach { (key, element) ->
                    if (element is JsonPrimitive) {
                        val primitive = element.jsonPrimitive
                        primitive.intOrNull?.also { prefs.putInt(key, it) }
                            ?: primitive.booleanOrNull?.also { prefs.putBoolean(key, it) }
                            ?: primitive.longOrNull?.also { prefs.putLong(key, it) }
                            ?: primitive.floatOrNull?.also { prefs.putFloat(key, it) }
                            ?: run {
                                if (primitive.isString) {
                                    prefs.putString(key, primitive.content)
                                }
                            }
                    } else if (element is JsonArray) {
                        val set = element.map { it.jsonPrimitive.content }.toSet()
                        prefs.putStringSet(key, set)
                    }
                }
            }
            prefs.commit()
        }
    }

    private fun generateId(key: String): String {
        val parts = key.split("/")
        val fixedKey = buildString {
            append(parts.first().lowercase() + "/")
            append(parts.get(1) + "/")
            if (parts.size == 2) {
                append("1")
            } else {
                append(parts.last())
            }
        }
        val bytes = MessageDigest.getInstance("MD5").digest(fixedKey.toByteArray())
        val id = (0..7).map { bytes[it].toLong() and 0xff shl 8 * (7 - it) }
            .reduce(Long::or) and Long.MAX_VALUE
        return id.toString()
    }
}
