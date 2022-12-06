package android.os

class Looper {
    companion object {
        @JvmStatic
        fun getMainLooper(): Looper = Looper()

        @JvmStatic
        fun myLooper(): Looper = Looper()
    }
}
