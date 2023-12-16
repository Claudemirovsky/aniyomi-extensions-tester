package app.cash.quickjs

import org.mozilla.javascript.ConsString
import org.mozilla.javascript.NativeArray
import java.io.Closeable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class QuickJs private constructor(private var engine: ScriptEngine?) : Closeable {
    companion object {
        @JvmStatic
        fun create() = QuickJs(ScriptEngineManager())
    }

    constructor(manager: ScriptEngineManager) : this(manager.getEngineByName("rhino"))

    @Suppress("UNUSED_PARAMETER")
    fun evaluate(script: String, fileName: String) = evaluate(script)

    fun evaluate(script: String): Any? {
        try {
            val value = engine?.eval(script)
            return translateType(value)
        } catch (exception: Exception) {
            throw QuickJsException(exception.message, exception)
        }
    }

    private fun translateType(obj: Any?): Any? {
        return when (obj) {
            is NativeArray -> obj.map(::translateType)
            is ConsString -> obj.toString()
            is Long -> obj.toInt()
            else -> obj
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun compile(sourceCode: String, fileName: String) = sourceCode.toByteArray()

    fun execute(bytecode: ByteArray) = evaluate(String(bytecode))

    override fun close() {
        engine = null
    }
}
