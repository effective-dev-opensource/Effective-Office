// See clients/shared/core/build.aurora.gradle.kts for why the convention plugins are avoided.
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.compose")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(project(":clients:tablet:core:data"))

            implementation(libs.aurora.lifecycle.viewmodel)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.bundles.aurora.koin)
            implementation(libs.aurora.kotlinx.datetime)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet.feature.fastBooking"
    generateResClass = auto
}
