// См. clients/shared/core/build.aurora.gradle.kts — почему без convention-плагинов.
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":clients:shared:core"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(libs.bundles.aurora.koin)
            implementation(libs.aurora.kotlinx.datetime)
            implementation(libs.bundles.aurora.ktor.client)
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}
