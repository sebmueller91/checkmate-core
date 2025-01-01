plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.github.sebmu91.checkmate"
version = "0.1.5-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.insert-koin:koin-core:4.0.0")

    testImplementation("io.insert-koin:koin-test:4.0.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("io.mockk:mockk:1.13.14")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}