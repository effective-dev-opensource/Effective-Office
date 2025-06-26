import band.effective.office.backend.libs
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.kotlin
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.sourceSets

plugins {
    id("band.effective.office.client.kmp.library")
    id("band.effective.office.client.kmp.ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(project(":clients:tablet:core:data"))

            implementation(libs.findLibrary("decompose").get())
            implementation(libs.findLibrary("decompose.compose.jetbrains").get())
        }
    }
}
