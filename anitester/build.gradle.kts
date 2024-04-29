import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask
import proguard.gradle.ProGuardTask
import java.io.BufferedReader

plugins {
    application
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.shadow)
}

dependencies {

    // Dependencies of Aniyomi, some are duplicate from root build.gradle.kts
    // keeping it here for reference
    implementation(libs.injekt.core)
    implementation(libs.jsoup)
    implementation(libs.rxjava)
    implementation(libs.bundles.okhttp)

    // AndroidCompat
    implementation(project(":AndroidCompat"))
    implementation(project(":AndroidCompat:Config"))

    // Cloudflare interceptor
    implementation(project(":playwright-utils"))

    // Testing
    testImplementation(kotlin("test-junit5"))
}

val MainClass = "anitester.MainKt"
application {
    mainClass.set(MainClass)
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
        }
    }
}

// should be bumped with each stable release
val anitesterVersion = "v2.6.1"

// counts commit count on master
val anitesterRevision = runCatching {
    System.getenv("ProductRevision") ?: Runtime
        .getRuntime()
        .exec(arrayOf("git", "rev-list", "HEAD", "--count"))
        .run {
            waitFor()
            val output = inputStream.bufferedReader().use(BufferedReader::readText)
            destroy()
            "r" + output.trim()
        }
}.getOrDefault("r0")

val String.wrapped get() = """"$this""""

buildConfig {
    className("BuildConfig")
    packageName("anitester")

    useKotlinOutput()

    buildConfigField("String", "NAME", rootProject.name.wrapped)
    buildConfigField("String", "VERSION", anitesterVersion.wrapped)
    buildConfigField("String", "REVISION", anitesterRevision.wrapped)
}

tasks {
    shadowJar {
        dependencies {
            exclude(
                // Useless icu-related files
                "com/ibm/icu/impl/data/icudt*/*/*",
                // Useless and heavy files from playwright-bundle
                "driver/*/node*",
                "driver/linux*/",
                "driver/mac-arm64/",
                "driver/win32_x64/",
            )
        }
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to MainClass,
                    "Implementation-Title" to rootProject.name,
                    "Implementation-Vendor" to "The Tachiyomi Open Source Project",
                    "Specification-Version" to anitesterVersion,
                    "Implementation-Version" to anitesterRevision,
                ),
            )
        }
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(anitesterVersion)
        archiveClassifier.set(anitesterRevision)
    }

    withType<KotlinCompile> {
        compilerOptions {
            optIn = listOf(
                "kotlin.RequiresOptIn",
                "kotlinx.coroutines.ExperimentalCoroutinesApi",
                "kotlinx.coroutines.InternalCoroutinesApi",
                "kotlinx.serialization.ExperimentalSerializationApi",
                "kotlin.io.path.ExperimentalPathApi",
            )
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }

    withType<ShadowJar> {
        destinationDirectory.set(File("$rootDir/anitester/build"))
        dependsOn("formatKotlin", "lintKotlin")
    }

    named("run") {
        dependsOn("formatKotlin", "lintKotlin")
    }

    named("test") {
        dependsOn("formatKotlin", "lintKotlin")
    }

    withType<LintTask> {
        exclude("**/BuildConfig.kt")
        source(files("src/kotlin"))
    }

    withType<FormatTask> {
        exclude("**/BuildConfig.kt")
        source(files("src/kotlin"))
    }

    withType<ProcessResources> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    register<ProGuardTask>("optimizeShadowJar") {
        group = "shadow"
        val shadowJar = getByName("shadowJar")
        dependsOn(shadowJar)
        val shadowJars = shadowJar.outputs.files
        injars(shadowJars)
        outjars(
            shadowJars.map { file ->
                File(file.parentFile, "min/" + file.name)
            },
        )
        val javaHome = System.getProperty("java.home")
        libraryjars("$javaHome/jmods")
        configuration("proguard-rules.pro")
    }
}
