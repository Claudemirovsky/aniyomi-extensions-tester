package xyz.nulldev.androidcompat.util

import android.content.Context
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import xyz.nulldev.androidcompat.androidimpl.CustomContext
import xyz.nulldev.androidcompat.androidimpl.FakePackageManager
import xyz.nulldev.androidcompat.info.ApplicationInfoImpl
import xyz.nulldev.androidcompat.io.AndroidFiles
import xyz.nulldev.androidcompat.pm.PackageController
import xyz.nulldev.androidcompat.service.ServiceSupport

/**
 * Helper class to allow access to Kodein from Java
 */
object KodeinGlobalHelper {
    /**
     * Get the Kodein object
     */
    @JvmStatic
    fun kodein() = DI.global

    /**
     * Get a dependency
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> instance(type: Class<T>, kodein: DI? = null): T {
        return when (type) {
            AndroidFiles::class.java -> getInjectedInstance<AndroidFiles>(kodein) as T
            ApplicationInfoImpl::class.java -> getInjectedInstance<ApplicationInfoImpl>(kodein) as T
            ServiceSupport::class.java -> getInjectedInstance<ServiceSupport>(kodein) as T
            FakePackageManager::class.java -> getInjectedInstance<FakePackageManager>(kodein) as T
            PackageController::class.java -> getInjectedInstance<PackageController>(kodein) as T
            CustomContext::class.java -> getInjectedInstance<CustomContext>(kodein) as T
            Context::class.java -> getInjectedInstance<Context>(kodein) as T
            else -> throw IllegalArgumentException("Kodein instance not found")
        }
    }

    private inline fun <reified A> getInjectedInstance(kodein: DI? = null): A {
        val instance: A by (kodein ?: kodein()).instance()
        return instance
    }

    @JvmStatic
    fun <T : Any> instance(type: Class<T>): T {
        return instance(type, null)
    }
}
