import band.effective.office.backend.libs
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.android
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.compose
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.kotlin
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.sourceSets

plugins {
    id("band.effective.office.client.kmp.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.findLibrary("coil").get())
            implementation(libs.findLibrary("coil.network.ktor").get())
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.findLibrary("androidx.activityCompose").get())
        }
    }
}
