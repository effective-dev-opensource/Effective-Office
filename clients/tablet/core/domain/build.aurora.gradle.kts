// См. clients/shared/core/build.aurora.gradle.kts — почему без convention-плагинов.
// multiplatform-settings под linux нет, поэтому SettingsStore получает свой linux-actual.
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
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}
