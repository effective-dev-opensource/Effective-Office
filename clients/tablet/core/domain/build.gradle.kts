plugins {
    id("band.effective.office.client.kmp.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":clients:shared:core"))
            api(libs.kotlinx.datetime)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.bundles.koin)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
            implementation(libs.koin.android)
            implementation(libs.settings)
        }
        iosMain.dependencies {
            implementation(libs.settings)
        }
    }
}
