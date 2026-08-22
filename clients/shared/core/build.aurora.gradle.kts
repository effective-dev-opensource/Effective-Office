// Aurora variant of the module. The build-logic convention plugins are deliberately not used here:
// they are what drags AGP and upstream Compose in, and neither shares an invocation with the fork.
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.aurora.koin)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.aurora.kotlinx.datetime)
            implementation(libs.bundles.aurora.ktor.client)
            implementation(libs.kotlinx.serialization.json)
        }

        linuxMain.dependencies {
            implementation(libs.aurora.ktor.client.curl)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}
