dependencyResolutionManagement {
    versionCatalogs {
        create("androidcompat") {
            from(files("gradle/androidcompat.versions.toml"))
        }
    }

    repositories {
        mavenCentral()
        maven("https://maven.google.com/")
        maven("https://jitpack.io")
        maven("https://dl.google.com/dl/android/maven2/")
        maven("https://repo1.maven.org/maven2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://github.com/Claudemirovsky/aniyomi-extensions-tester/raw/android-jar/")
    }
}

rootProject.name = "aniyomi-extensions-tester"
include(":server")
include(":AndroidCompat")
include(":AndroidCompat:Config")
