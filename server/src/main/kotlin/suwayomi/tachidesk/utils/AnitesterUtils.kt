package suwayomi.tachidesk.utils

import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.nulldev.androidcompat.androidimpl.StubbedCookieManager
import java.nio.file.Path
import kotlin.io.path.notExists
import kotlin.io.path.readLines

object AnitesterUtils {
    fun loadCookies(file: Path) {
        if (file.notExists()) return
        val httpOnlyStr = "#HttpOnly_"
        val cookieMap = file.readLines().mapNotNull { line ->
            when {
                line.startsWith(httpOnlyStr) || !line.startsWith("#") -> {
                    val params = line.split("\t")
                    if (params.size < 7) return@mapNotNull null
                    runCatching {
                        Cookie.Builder().apply {
                            val host = params.first().let {
                                if (it.startsWith(httpOnlyStr)) httpOnly()
                                it.substringAfter(httpOnlyStr).trim('.')
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
        }.groupBy { it.domain }

        val manager = StubbedCookieManager()

        cookieMap.entries.forEach { (domain, cookies) ->
            manager.addAll("https://$domain".toHttpUrl(), cookies)
        }
    }
}
