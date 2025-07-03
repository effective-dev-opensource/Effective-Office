import band.effective.office.backend.libs

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
            api("com.mohamedrejeb.calf:calf-ui:0.8.0")
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.findLibrary("androidx.activityCompose").get())
        }
    }
}
