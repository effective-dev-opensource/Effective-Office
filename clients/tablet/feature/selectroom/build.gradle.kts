plugins {
    id("band.effective.office.client.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.0")
        }
    }
}
