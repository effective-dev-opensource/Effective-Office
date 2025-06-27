plugins {
    id("band.effective.office.client.kmp.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.koin)
        }
    }
}
