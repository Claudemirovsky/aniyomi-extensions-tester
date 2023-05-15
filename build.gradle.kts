import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.buildconfig) apply false
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
    project(":server")
)

configure(projects) {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    dependencies {
        val libs = rootProject.libs
        val androidcompat = rootProject.androidcompat

        // Kotlin
        implementation(kotlin("stdlib-jdk8"))
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
