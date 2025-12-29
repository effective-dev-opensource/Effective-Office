plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-kapt")
}

android {
    namespace = "band.effective.office.smsrouter"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "band.effective.office.smsrouter"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activityCompose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.compose)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor.client)

    implementation(libs.napier)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(project(":clients:shared:core"))
    implementation("org.slf4j:slf4j-android:1.7.36")
}
