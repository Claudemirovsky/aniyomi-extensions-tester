import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.shadow) apply false
}

buildscript {
    dependencies {
        classpath(libs.proguard) {
            exclude("com.android.tools.build")
        }
    }
}

allprojects {
    group = "suwayomi"
    version = "1.0"
}

val projects = listOf(
    project(":playwright-utils"),
    project(":AndroidCompat"),
    project(":AndroidCompat:Config"),
    project(":server"),
)

configure(projects) {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    val javaVersion = JavaVersion.VERSION_11

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    detekt {
        parallel = true
        ignoreFailures = true
        buildUponDefaultConfig = true
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = javaVersion.toString()

        reports {
            html.required = true
        }
        basePath = rootDir.absolutePath
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
        }
    }

    dependencies {
        val libs = rootProject.libs
        val androidcompat = rootProject.androidcompat

        // Kotlin
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        // :AndroidCompat:Config dependency
        implementation(androidcompat.bundles.config)

        // :AndroidCompat dependency to get application content root
        implementation(androidcompat.appdirs)

        // APK parser
        implementation(libs.apkparser)

        // CLI
        implementation(libs.kotlinx.cli)

        // Coroutines
        implementation(libs.bundles.coroutines)

        // Dependency Injection
        implementation(libs.kodein.di)
        implementation(libs.injekt.core)

        // Dex2Jar
        implementation(libs.bundles.dex2jar)

        // JSoup
        implementation(libs.jsoup)

        // Logging
        implementation(libs.bundles.logging)

        // OkHttp3
        implementation(libs.bundles.okhttp)

        // Playwright
        implementation(libs.playwright)

        // ReactiveX
        implementation(libs.rxjava)

        // Serialization
        implementation(libs.bundles.serialization)
    }
}
