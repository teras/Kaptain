plugins {
    application
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21" // Or match your Kotlin compiler
    id("io.ktor.plugin") version "3.1.3"
}

application {
    mainClass.set("onl.ycode.kaptain.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.1.3")
    implementation("io.ktor:ktor-server-netty:3.1.3")
    implementation("io.ktor:ktor-server-html-builder:3.1.3")
    implementation("io.ktor:ktor-server-sse:3.1.3")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.mapdb:mapdb:3.1.0")

}
