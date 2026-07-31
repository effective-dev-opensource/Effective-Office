plugins {
    id("band.effective.office.client.kmp.ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":clients:shared:core"))
            api(libs.kotlinx.datetime)
            implementation(libs.napier)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "band.effective.office.tablet.core.ui"
    generateResClass = auto
}