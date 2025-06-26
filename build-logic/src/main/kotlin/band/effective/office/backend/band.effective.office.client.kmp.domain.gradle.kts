import band.effective.office.backend.libs
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.kotlin
import gradle.kotlin.dsl.accessors._628e4eb050456810d51173b39b973811.sourceSets

plugins {
    id("band.effective.office.client.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("kotlinx.datetime").get())
        }
    }
}
