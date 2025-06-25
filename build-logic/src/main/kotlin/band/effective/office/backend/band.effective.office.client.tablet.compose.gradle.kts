import band.effective.office.backend.libs
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    //alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

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

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.findLibrary("kotlin.coroutines.core").get())
            implementation(libs.findLibrary("kotlinx.serialization.json").get())
            implementation(libs.findLibrary("coil").get())
            implementation(libs.findLibrary("coil.network.ktor").get())

//            getLibraryFromLibsToml("kotlinx.datetime")
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)

            implementation(libs.findLibrary("androidx.activityCompose").get())

            implementation(libs.findLibrary("coroutines.android").get())
        }
    }
}

android {
    namespace = "band.effective.office.tablet"
    compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()
        targetSdk = libs.findVersion("android.targetSdk").get().requiredVersion.toInt()

        applicationId = "band.effective.office.tablet.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}


compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet"
    generateResClass = auto
}
