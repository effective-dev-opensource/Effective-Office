plugins {
    id("band.effective.office.client.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tablet:feature:slot"))
            implementation(compose.components.uiToolingPreview)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.main"
    generateResClass = auto
}
