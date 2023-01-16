-dontobfuscate
-optimizations !library/gson
-dontnote **
-keepattributes Signature,LineNumberTable

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##---------------Begin: proguard configuration for kotlinx.serialization  --------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class eu.kanade.tachiyomi.**$$serializer { *; }
-keepclassmembers class eu.kanade.tachiyomi.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kanade.tachiyomi.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class suwayomi.tachidesk.**$$serializer { *; }
-keepclassmembers class suwayomi.tachidesk.** {
    *** Companion;
}
-keepclasseswithmembers class suwayomi.tachidesk.** {
    kotlinx.serialization.KSerializer serializer(...);
}

##---------------End: proguard configuration for kotlinx.serialization  ----------

# Keep needed things to run the .jar
-keep,allowoptimization class suwayomi.tachidesk.** { *; }
-keep class suwayomi.tachidesk.MainKt {
    public static void main(java.lang.String[]);
}
-keepdirectories suwayomi/tachidesk/**
-keepdirectories META-INF/**
-keepdirectories driver/**
-keepdirectories cloudflare-js/**

# Keep extensions dependencies
-keep class app.cash.quickjs.** { public protected *; }
-keep class eu.kanade.tachiyomi.** { *; }
-keep class kotlin.** { public protected *; }
-keep class kotlinx.coroutines.** { public protected *; }
-keep class kotlinx.serialization.** { public protected *; }
-keep class okhttp3.** { public protected *; }
-keep class org.jsoup.** { *; }
-keep,allowoptimization class androidx.preference.** { public protected *; }
-keep,allowoptimization class okio.** { public protected *; }
-keep,allowoptimization class org.jsoup.nodes.** { public protected *; }
-keep,allowoptimization class org.jsoup.select.** { public protected *; }
-keep,allowoptimization class rx.** { public protected *; }
-keep,allowoptimization class uy.kohesive.injekt.** { public protected *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

# OKHTTP
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.**

# Xml
-dontwarn com.sun.org.apache.**
-dontwarn javax.xml.**
-dontwarn org.apache.xerces.**
-dontwarn org.xml.sax.**
-keep class org.apache.xerces.** { public *; }

# org.json
-dontwarn org.json.JSONWriter
-dontwarn org.json.XMLTokener
-keep class org.json.JSONObject { *; }

# Android
-dontwarn android.**
-dontwarn androidx.annotation.*
-dontwarn com.android.**
-keep class android.net.Uri { public protected *; }
-keep class android.os.Handler { *; }
-keep class android.os.Looper { *; }
-keep class android.util.Base64 { public protected *; }
-keep class android.webkit.CookieManager { public protected *; }
-keep class android.webkit.WebView { public *; }
-keep,allowoptimization class android.** { public protected *; }

# Logging
-dontwarn ch.qos.logback.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.slf4j.MDC
-dontwarn org.slf4j.MarkerFactory
-keep class ch.qos.logback.** { *; }
-keep class mu.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class org.slf4j.** { *; }

# Java
-dontwarn java.util.prefs.**
-dontwarn javax.annotation.**
-dontwarn javax.imageio.**
-dontwarn javax.swing.**

# GraalVM
-dontwarn com.oracle.svm.core.configure.ResourcesRegistry
-dontwarn org.graalvm.nativeimage.**

# BouncyCastle
-dontwarn org.bouncycastle.cert.**
-dontwarn org.bouncycastle.cms.**
-dontwarn org.bouncycastle.jce.provider.BouncyCastleProvider
-dontwarn org.bouncycastle.util.Store

# HtmlUnit
-keep class com.gargoylesoftware.htmlunit.** { *; }
-keep class org.apache.http.impl.client.** { *; }
-keep class org.mozilla.** { *; }
-keep,allowoptimization class net.sourceforge.htmlunit.corejs.** { *; }

# commons-compress
-dontwarn org.apache.commons.compress.**
-keep class com.microsoft.playwright.** { *; }
-keep class com.google.gson.JsonElement { *; }
-keep class com.google.gson.JsonObject { *; }

# Other
-dontwarn com.oracle.svm.core.annotate.**
-dontwarn com.sun.rowset.internal.*
-dontwarn edu.umd.cs.findbugs.**
-dontwarn org.antlr.runtime.tree.DOTTreeGenerator
-dontwarn org.eclipse.jetty.**

