plugins {
    id("band.effective.office.client.kmp.data")
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.koin)
            implementation(libs.decompose)
            implementation(libs.essenty.lifecycle)
            implementation(libs.kotlin.coroutines.core)
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}
