plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.github.sebmu91.checkmate"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.insert-koin:koin-core:4.0.0")
    testImplementation("io.insert-koin:koin-test:4.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}