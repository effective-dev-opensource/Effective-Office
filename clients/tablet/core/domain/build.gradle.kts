plugins {
    id("band.effective.office.client.kmp.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.0")
            implementation(libs.kotlin.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
        }
    }
}
