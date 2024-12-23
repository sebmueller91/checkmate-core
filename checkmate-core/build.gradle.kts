plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.github.sebmu91.checkmate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}