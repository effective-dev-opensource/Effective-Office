plugins {
    id("band.effective.office.client.kmp.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:shared:core"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(libs.bundles.koin)
        }
    }
}
