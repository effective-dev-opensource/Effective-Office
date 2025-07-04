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

            implementation(libs.findLibrary("decompose").get())
            implementation(libs.findLibrary("decompose.compose.jetbrains").get())
            implementation(libs.findLibrary("essenty.lifecycle").get())
            implementation(libs.findLibrary("essenty.state.keeper").get())

            implementation(libs.findLibrary("kotlin.coroutines.core").get())

            implementation(libs.findBundle("koin").get())
            implementation(libs.findLibrary("kotlinx-datetime").get())
        }
        androidMain.dependencies {
            implementation(libs.findLibrary("koin-android").get())
        }
        iosMain.dependencies {
            implementation(libs.findLibrary("essenty.darwin.runtime").get())
        }
    }
}
