plugins {
    id("band.effective.office.client.kmp.data")
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.koin)
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}
