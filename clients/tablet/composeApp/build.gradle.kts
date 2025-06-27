import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.decompose)
            implementation(libs.decompose.compose.jetbrains)

            implementation(libs.kotlin.coroutines.core)

            implementation(libs.bundles.koin)

            implementation(libs.napier)

            implementation(project(":clients:core:data"))

            implementation(project(":clients:tablet:core:data"))
            implementation(project(":clients:tablet:core:domain"))
            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:feature:main"))
            implementation(project(":clients:tablet:feature:settings"))
            implementation(project(":clients:tablet:feature:roominfo"))
            implementation(project(":clients:tablet:feature:selectroom"))
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.koin.android)
        }
    }
}

android {
    namespace = "band.effective.office.tablet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        applicationId = "band.effective.office.tablet.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildFeatures {
            buildConfig = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

val apiUrlRelease: String = gradleLocalProperties(rootDir, providers).getProperty("api.url.release")
val apiUrlDebug: String = gradleLocalProperties(rootDir, providers).getProperty("api.url.debug")

buildkonfig {
    packageName = "band.effective.office.tablet"
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "API_URL_RELEASE",
            apiUrlRelease,
        )

        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "API_URL_DEBUG",
            apiUrlDebug,
        )
    }
}
