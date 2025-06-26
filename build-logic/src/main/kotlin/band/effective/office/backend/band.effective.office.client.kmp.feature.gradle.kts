import band.effective.office.backend.libs
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.kotlin
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.sourceSets

plugins {
    id("band.effective.office.client.kmp.library")
    id("band.effective.office.client.kmp.ui")
    id("com.arkivanov.parcelize.darwin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(project(":clients:tablet:core:data"))

            implementation(libs.findLibrary("decompose").get())
            implementation(libs.findLibrary("decompose.compose.jetbrains").get())
            implementation(libs.findLibrary("essenty.lifecycle").get())
            implementation(libs.findLibrary("essenty.state.keeper").get())

            implementation(libs.findLibrary("kotlin.coroutines.core").get())
        }
        iosMain.dependencies {
            implementation(libs.findLibrary("essenty.darwin.runtime").get())
        }
    }
}
