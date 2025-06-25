import band.effective.office.backend.libs
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.kotlin

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}

android {
    namespace = "band.effective.office.tablet"
    compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()
        targetSdk = libs.findVersion("android.targetSdk").get().requiredVersion.toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}