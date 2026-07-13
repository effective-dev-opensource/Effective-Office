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
    val kotlinVersion = libs.versions.kotlin.get()
    val springBootVersion = libs.versions.springBoot.get()
    val springDepManagementVersion = libs.versions.springDependencyManagement.get()
    val agpVersion = libs.versions.agp.get()
    val composeGradlePluginVersion = libs.versions.compose.get()
    val googleServicesVersion = libs.versions.googleServices.get()

    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:$kotlinVersion")
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:$springBootVersion")
    implementation("org.jetbrains.kotlin.plugin.spring:org.jetbrains.kotlin.plugin.spring.gradle.plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin.plugin.jpa:org.jetbrains.kotlin.plugin.jpa.gradle.plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("io.spring.gradle:dependency-management-plugin:$springDepManagementVersion")

    implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:$kotlinVersion")
    implementation("com.android.tools.build:gradle:$agpVersion") {
        exclude(group = "org.apache.commons", module = "commons-compress")
    }
    implementation("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:$composeGradlePluginVersion")
    implementation("com.google.gms:google-services:$googleServicesVersion")
    implementation("org.jetbrains.kotlin.kapt:org.jetbrains.kotlin.kapt.gradle.plugin:$kotlinVersion")
}