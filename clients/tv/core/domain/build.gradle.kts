plugins {
    id("band.effective.office.client.kmp.domain")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    
    jvm("desktop")
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.datetime)
                implementation(libs.kotlin.coroutines.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.coroutines.android)
            }
        }
    }
}


