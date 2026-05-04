@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io") // for cvb941/kchesslib (JVM test oracle only)
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "checkmate"

include(":core")

// Future phases — uncomment when each module is added (see plan, phases v0.2+):
include(":server")    // v0.2 — Ktor backend
// include(":android")   // v0.5 — Compose app
// :ios is bootstrapped via Xcode + KMP framework export, not via include()
