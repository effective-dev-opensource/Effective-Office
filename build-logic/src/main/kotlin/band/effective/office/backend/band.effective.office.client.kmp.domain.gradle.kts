import band.effective.office.backend.libs

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
