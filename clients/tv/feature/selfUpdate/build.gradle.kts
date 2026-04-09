plugins {
    id("band.effective.office.client.kmp.library")
    id("band.effective.office.client.kmp.ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:shared:core"))
            implementation(project(":clients:tv:core:ui"))

            implementation(libs.decompose)
            implementation(libs.decompose.compose.jetbrains)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.state.keeper)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "band.effective.office.tv.feature.selfUpdate"
}