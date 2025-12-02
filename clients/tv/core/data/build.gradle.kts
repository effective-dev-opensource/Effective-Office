plugins {
    id("band.effective.office.client.kmp.data")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    
    jvm("desktop")
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":clients:tv:core:domain"))
                implementation(libs.bundles.koin)
            }
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
    }
}


