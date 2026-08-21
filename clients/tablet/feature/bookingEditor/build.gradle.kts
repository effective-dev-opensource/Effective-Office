plugins {
    id("band.effective.office.client.kmp.feature")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.calf.ui)
        }
        iosMain.dependencies {
            implementation(libs.calf.ui)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.bookingEditor"
    generateResClass = auto
}