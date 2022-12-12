dependencies {
    // APK signature verifier
    compileOnly(androidcompat.apksig)

    // Android stub library
    //implementation(fileTree("lib/"))
    implementation(androidcompat.android.jar)

    // Android version of SimpleDateFormat
    implementation(androidcompat.icu4j)

    // AndroidX annotations
    compileOnly(androidcompat.android.annotations)

    // Config API
    implementation(project(":AndroidCompat:Config"))

    // Jackson annotations
    implementation(androidcompat.jackson.annotations)

    // Kotlin wrapper around Java Preferences, makes certain things easier
    implementation(androidcompat.bundles.settings)

    // Rhino, an pure-java alternative to duktape-android / QuickJS
    implementation(androidcompat.bundles.rhino)

    // XML
    compileOnly(androidcompat.xmlpull)
}
