plugins {
    id("band.effective.office.client.kmp.library")
    id("band.effective.office.client.kmp.ui")
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tv:core:ui"))
            implementation(project(":clients:tv:core:domain"))

            implementation(libs.decompose)
            implementation(libs.decompose.compose.jetbrains)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.state.keeper)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tv.feature.menu"
    generateResClass = auto
}
