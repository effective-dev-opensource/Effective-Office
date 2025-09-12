import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    alias(libs.plugins.buildkonfig)
    id("com.google.gms.google-services") version "4.4.3"
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

            export(libs.decompose)
            export(libs.decompose.compose.jetbrains)
            export(libs.essenty.lifecycle)

            export("com.mohamedrejeb.calf:calf-ui:0.8.0")
        }
        it.compilations.all {
            kotlinOptions.freeCompilerArgs += "-Xdisable-decompose-parcelize"
        }

    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            api(libs.decompose)
            api(libs.decompose.compose.jetbrains)
            api("com.mohamedrejeb.calf:calf-ui:0.8.0")
            api(libs.kotlinx.datetime)

            implementation(libs.kotlin.coroutines.core)

            implementation(libs.bundles.koin)

            implementation(libs.napier)

            implementation(libs.settings)

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
        }
    }
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

        signingConfigs {
            getByName("debug") {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = file("${rootDir}/keystore/debug.keystore")
                storePassword = "android"
            }
            create("release") {
                keyAlias = System.getenv()["OFFICE_ELEVATOR_RELEASE_ALIAS"]
                keyPassword = System.getenv()["OFFICE_ELEVATOR_RELEASE_KEY_PASSWORD"]
                storeFile = file("${rootDir}/keystore/main.keystore")
                storePassword = System.getenv()["OFFICE_ELEVATOR_RELEASE_STORE_PASSWORD"]
            }
        }

        buildTypes {
            getByName("debug") {
                signingConfig = signingConfigs.getByName("debug")
                isDebuggable = true
            }
            getByName("release") {
                signingConfig = signingConfigs.getByName("debug")
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
