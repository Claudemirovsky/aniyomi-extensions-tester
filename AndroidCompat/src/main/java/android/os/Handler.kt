package android.os

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Handler(private val looper: Looper) {

    fun post(runnable: Runnable): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            runnable.run()
        }
        return true
    }
}
