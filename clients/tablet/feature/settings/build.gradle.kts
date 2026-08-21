plugins {
    id("band.effective.office.client.kmp.feature")
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.settings"
    generateResClass = auto
}