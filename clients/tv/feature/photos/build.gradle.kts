plugins {
    id("band.effective.office.client.kmp.library")
    id("band.effective.office.client.kmp.ui")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:shared:core"))
            implementation(project(":clients:tv:core:ui"))
            implementation(project(":clients:tv:core:data"))

            implementation(libs.decompose)
            implementation(libs.decompose.compose.jetbrains)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.state.keeper)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.ktor.client)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "band.effective.office.tv.feature.photos"
    generateResClass = auto
}

android {
    namespace = "band.effective.office.tv.feature.photos"
}

