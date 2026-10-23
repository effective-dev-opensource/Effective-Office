import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.google.services)
    id("sign-config")
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

            api(libs.kotlinx.datetime)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlin.coroutines.core)

            implementation(libs.bundles.koin)

            implementation(libs.napier)

            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:feature:main"))
            implementation(project(":clients:tablet:feature:settings"))
            implementation(project(":clients:tablet:feature:bookingEditor"))
            implementation(project(":clients:tablet:feature:fastBooking"))
            implementation(project(":clients:tablet:feature:slot"))

            implementation(project(":clients:tablet:core:data"))
            implementation(project(":clients:tablet:core:domain"))
        }
        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.koin.android)
            implementation("org.slf4j:slf4j-android:1.7.36")
            implementation(libs.firebase.messaging.ktx)
            implementation(libs.settings)
        }
        iosMain.dependencies {
            implementation(libs.settings)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "band.effective.office.tablet"
    generateResClass = auto
}

android {
    namespace = "band.effective.office.tablet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        applicationId = "band.effective.office.tablet"
        versionCode = 3
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildFeatures {
            buildConfig = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        buildTypes {
            getByName("debug") {
                isDebuggable = true
            }
            getByName("release") {
                isDebuggable = false
                isMinifyEnabled = false
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/INDEX.LIST"
        }
    }
}

val apiUrlRelease: String = gradleLocalProperties(rootDir, providers).getProperty("api.url.release")
val apiUrlDebug: String = gradleLocalProperties(rootDir, providers).getProperty("api.url.debug")
val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("apiKey")
val appVersionName: String = android.defaultConfig.versionName!!

buildkonfig {
    packageName = "band.effective.office.tablet"
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "VERSION_NAME",
            appVersionName,
        )
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

        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "API_KEY",
            apiKey,
        )
    }
}
