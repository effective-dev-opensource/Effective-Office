plugins {
    id("band.effective.office.client.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tablet:feature:bookingEditor"))
            implementation(project(":clients:tablet:feature:fastBooking"))
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.main"
    generateResClass = auto
}