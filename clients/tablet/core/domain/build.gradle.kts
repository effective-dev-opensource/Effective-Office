plugins {
    id("band.effective.office.client.kmp.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":clients:shared:core"))
            api(libs.kotlinx.datetime)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.settings)
            implementation(libs.bundles.koin)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
            implementation(libs.koin.android)
        }
    }
}
