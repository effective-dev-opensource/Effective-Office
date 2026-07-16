import band.effective.office.backend.libs

plugins {
    id("band.effective.office.client.kmp.library")
    id("band.effective.office.client.kmp.ui")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(project(":clients:tablet:core:data"))

            implementation(libs.findLibrary("jetbrains-lifecycle-viewmodel").get())

            implementation(libs.findLibrary("kotlin.coroutines.core").get())

            implementation(libs.findBundle("koin").get())
            implementation(libs.findLibrary("kotlinx-datetime").get())
        }
        androidMain.dependencies {
            implementation(libs.findLibrary("koin-android").get())
        }
    }
}
