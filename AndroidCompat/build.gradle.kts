dependencies {
    // Android stub library
    implementation("com.github.Suwayomi:android-jar:1.0.0")

    // XML
    compileOnly("xmlpull:xmlpull:1.1.3.4a")

    // Config API
    implementation(project(":AndroidCompat:Config"))

    // APK sig verifier
    compileOnly("com.android.tools.build:apksig:7.2.1")

    // AndroidX annotations
    compileOnly("androidx.annotation:annotation:1.5.0")

    // Jackson annotations
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.4")

    // substitute for duktape-android
    implementation("org.mozilla:rhino-runtime:1.7.14") // slimmer version of 'org.mozilla:rhino'
    implementation("org.mozilla:rhino-engine:1.7.14") // provides the same interface as 'javax.script' a.k.a Nashorn

    // Kotlin wrapper around Java Preferences, makes certain things easier
    val multiplatformSettingsVersion = "1.0.0-RC"
    implementation("com.russhwolf:multiplatform-settings-jvm:$multiplatformSettingsVersion")
    implementation("com.russhwolf:multiplatform-settings-serialization-jvm:$multiplatformSettingsVersion")

    // Android version of SimpleDateFormat
    implementation("com.ibm.icu:icu4j:72.1")
}
