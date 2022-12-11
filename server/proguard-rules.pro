-dontobfuscate
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

# Keep extensions dependencies
-keep,allowoptimization class androidx.preference.** { public protected *; }
-keep,allowoptimization class okio.** { public protected *; }
-keep,allowoptimization class rx.** { public protected *; }
-keep,allowoptimization class uy.kohesive.injekt.** { public protected *; }
-keep class app.cash.quickjs.** { public protected *; }
-keep class eu.kanade.tachiyomi.** { *; }
-keep class kotlin.** { public protected *; }
-keep class kotlinx.coroutines.** { public protected *; }
-keep class kotlinx.serialization.** { public protected *; }
-keep class okhttp3.** { public protected *; }
-keep,allowoptimization class org.jsoup.nodes.** { public protected *; }
-keep,allowoptimization class org.jsoup.select.** { public protected *; }
-keep class org.jsoup.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

# OKHTTP
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.**

# Xml
-keep class org.apache.xerces.** { public *; }
-dontwarn javax.xml.**
-dontwarn org.xml.sax.**
-dontwarn com.sun.org.apache.**
-dontwarn org.apache.xerces.**

# org.json
-dontwarn org.json.XMLTokener
-dontwarn org.json.JSONWriter
-keep class org.json.JSONObject { *; }

# Android
-dontwarn com.android.**
-keep,allowoptimization class android.** { public protected *; }
-keep class android.net.Uri { public protected *; }
-keep class android.os.Looper { *; }
-keep class android.os.Handler { *; }
-keep class android.util.Base64 { public protected *; }
-keep class android.webkit.CookieManager { public protected *; }
-keep class android.webkit.WebView { public *; }
-dontwarn android.**
-dontwarn androidx.annotation.*

# Logback
-keep class ch.qos.logback.** { *; }
-keep,allowoptimization class org.apache.commons.logging.** { *; }
-dontwarn org.apache.commons.logging.**
-dontwarn ch.qos.logback.**
-dontwarn org.slf4j.MDC
-dontwarn org.slf4j.MarkerFactory

# Java
-dontwarn javax.imageio.**
-dontwarn javax.swing.**
-dontwarn java.util.prefs.**
-dontwarn javax.annotation.**

# GraalVM
-dontwarn org.graalvm.nativeimage.**
-dontwarn com.oracle.svm.core.configure.ResourcesRegistry

# BouncyCastle
-dontwarn org.bouncycastle.cert.**
-dontwarn org.bouncycastle.cms.**
-dontwarn org.bouncycastle.util.Store
-dontwarn org.bouncycastle.jce.provider.BouncyCastleProvider

# HtmlUnit
-keep,allowoptimization class com.gargoylesoftware.htmlunit.** { *; }
-keep,allowoptimization class net.sourceforge.htmlunit.corejs.** { *; }
-keep,allowoptimization class org.apache.http.impl.client.** { *; }
-keep class org.mozilla.** { *; }

# Other
-dontwarn edu.umd.cs.findbugs.**
-dontwarn com.oracle.svm.core.annotate.**
-dontwarn org.eclipse.jetty.**
-dontwarn org.antlr.runtime.tree.DOTTreeGenerator
-dontwarn com.sun.rowset.internal.*
