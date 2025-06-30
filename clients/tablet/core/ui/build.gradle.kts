plugins {
    id("band.effective.office.client.kmp.ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.epicarchitect:epic-calendar-compose:1.0.8")
            implementation(libs.kotlinx.datetime)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "band.effective.office.tablet.core.ui"
    generateResClass = auto
}