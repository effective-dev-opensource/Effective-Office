plugins {
    id("band.effective.office.client.kmp.ui")
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(project(":clients:shared:core"))
            api(libs.kotlinx.datetime)
            implementation(libs.decompose)
            implementation(libs.decompose.compose.jetbrains)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.state.keeper)
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
    packageOfResClass = "band.effective.office.tv.core.ui"
    generateResClass = auto
}