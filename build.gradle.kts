import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"

    application
}

group = "eu.shubert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("ch.qos.logback:logback-core:1.4.1")
    api("ch.qos.logback:logback-classic:1.4.1")
    implementation("org.json:json:20220320")
    api("org.slf4j:slf4j-api:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}