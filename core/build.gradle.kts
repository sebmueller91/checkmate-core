plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

group = "dev.smueller.checkmate"
version = "0.1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)

    jvm()

    sourceSets.all {
        languageSettings {
            optIn("kotlin.ExperimentalUnsignedTypes")
        }
    }

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    // androidTarget() is deferred to v0.4 (just before :android in v0.5).
    // Adding it later only requires uncommenting it here and applying the
    // android-library plugin. No source moves required.

    sourceSets {
        commonMain.dependencies {
            // Empty for v0.1. Wire in libs.kotlinx.serialization.core when the
            // WS protocol DTOs land in v0.2.
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        // jvmTest.dependencies { implementation(libs.kchesslib) }
        // ^ kchesslib oracle test wired in later (see plan task #12) once a
        //   resolvable artifact coordinate is confirmed.
    }
}
