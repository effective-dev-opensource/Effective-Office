import band.effective.office.backend.libs

plugins {
    id("band.effective.office.client.kmp.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.findBundle("ktor.client").get())
            implementation(libs.findLibrary("kotlin.coroutines.core").get())
            implementation(libs.findLibrary("kotlinx.serialization.json").get())
        }
        androidMain.dependencies {
            implementation(libs.findLibrary("ktor.client.okhttp").get())
            implementation(libs.findLibrary("coroutines.android").get())
        }
        iosMain.dependencies {
            implementation(libs.findLibrary("ktor.client.darwin").get())
        }
    }
}
