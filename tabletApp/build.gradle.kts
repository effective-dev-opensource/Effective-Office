import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(Plugins.Android.plugin)
    id(Plugins.MultiplatformCompose.plugin)
    id(Plugins.Kotlin.plugin)
    id(Plugins.Parcelize.plugin)
    id(Plugins.Libres.plugin)
    id(Plugins.GoogleServices.plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

android {
    namespace = "band.effective.office.tablet"
    compileSdk = 34

    defaultConfig {
        applicationId = "band.effective.office.tablet"
        versionCode = 4
        versionName = "1.1.0"

        minSdk = 26
        targetSdk = 34
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
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

kotlin {
    androidTarget()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)

                // Decompose
                implementation(Dependencies.Decompose.decompose)
                implementation(Dependencies.Decompose.extensions)

                //Libres
                implementation(Dependencies.Libres.libresCompose)

                // MVI Kotlin
                api(Dependencies.MviKotlin.mviKotlin)
                api(Dependencies.MviKotlin.mviKotlinMain)
                api(Dependencies.MviKotlin.mviKotlinExtensionsCoroutines)

                implementation(project(":tabletApp:features:selectRoom"))
                implementation(project(":tabletApp:features:roomInfo"))
                implementation(project(":tabletApp:features:core"))
                implementation(project(":tabletApp:features:di"))

            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.AndroidX.appCompat)
                implementation(Dependencies.AndroidX.activityCompose)
                implementation(Dependencies.Compose.uiTooling)
                implementation("com.google.firebase:firebase-messaging-ktx:23.1.0")
            }
        }
    }
}
