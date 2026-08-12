plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            api(project(":clients:shared:core"))
            api(libs.aurora.kotlinx.datetime)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.bundles.aurora.koin)
        }

        linuxMain.dependencies {
            // Persistent settings. multiplatform-settings has no linux target, so this is what
            // backs SettingsStore here — see domainModule.linux.kt.
            implementation(libs.aurora.ak.shared.preferences)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}
