// Aurora-вариант модуля. Convention-плагины из build-logic здесь не используются: они тянут
// AGP и upstream Compose, которые в одной инвокации с форком не уживаются.
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
