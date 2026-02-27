plugins {
    id("band.effective.office.client.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:tablet:feature:slot"))
            implementation("org.jetbrains.compose.components:components-ui-tooling-preview:1.10.0")

        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.main"
    generateResClass = auto
}
