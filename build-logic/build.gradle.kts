plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven(url = "https://androidx.dev/storage/compose-compiler/repository")
    maven("https://jitpack.io")
    maven(url = "https://dl.google.com/dl/android/maven2/")
    gradlePluginPortal()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.1.21")
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:3.5.0")
    implementation("org.jetbrains.kotlin.plugin.spring:org.jetbrains.kotlin.plugin.spring.gradle.plugin:2.1.21")
    implementation("org.jetbrains.kotlin.plugin.jpa:org.jetbrains.kotlin.plugin.jpa.gradle.plugin:2.1.21")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.1.21")
    implementation("org.jetbrains.kotlin:kotlin-noarg:2.1.21")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.5.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.7")

    implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.1.21")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.1.21")
    implementation("org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:2.1.21")
    implementation("com.android.tools.build:gradle:8.9.1")
    implementation("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:1.8.1")
    implementation("com.google.gms:google-services:4.4.3")
}