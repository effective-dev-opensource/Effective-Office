import band.effective.office.backend.libs

plugins {
    id("band.effective.office.client.kmp.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("kotlinx.datetime").get())
        }
    }
}
