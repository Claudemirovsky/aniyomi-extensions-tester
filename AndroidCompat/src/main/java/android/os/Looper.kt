package android.os

@Suppress("UtilityClassWithPublicConstructor")
class Looper {
    companion object {
        @JvmStatic
        fun getMainLooper(): Looper = Looper()

        @JvmStatic
        fun myLooper(): Looper = Looper()
    }
}
