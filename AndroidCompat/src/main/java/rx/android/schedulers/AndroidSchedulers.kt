package rx.android.schedulers

import rx.Scheduler
import rx.internal.schedulers.ImmediateScheduler

@Suppress("UtilityClassWithPublicConstructor")
class AndroidSchedulers {
    companion object {
        val mainThreadScheduler by lazy {
            ImmediateScheduler.INSTANCE!!
        }

        /**
         * Simulated main thread scheduler
         */
        @JvmStatic
        fun mainThread(): Scheduler = mainThreadScheduler
    }
}
