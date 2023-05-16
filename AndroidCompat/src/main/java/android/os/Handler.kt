package android.os

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class Handler(val looper: Looper) {
    constructor(handler: Handler) : this(handler.looper)
    constructor() : this(Looper())

    interface Callback {
        fun handleMessage(message: Message): Boolean = true
    }

    fun post(runnable: Runnable): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            runnable.run()
        }
        return true
    }

    open fun handleMessage(message: Message) {}
}
