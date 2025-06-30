plugins {
    id("band.effective.office.client.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.epicarchitect:epic-calendar-compose:1.0.8")
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.main"
    generateResClass = auto
}