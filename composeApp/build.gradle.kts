import dev.icerock.gradle.MRVisibility.Public
import org.jetbrains.kotlin.config.JvmTarget

plugins {
    id(Plugins.Kotlin.plugin)
    id(Plugins.MultiplatformCompose.plugin)
    id(Plugins.CocoaPods.plugin)
    id(Plugins.Android.plugin)
    id(Plugins.SQLDelight.plugin) version Plugins.SQLDelight.version
    id(Plugins.BuildConfig.plugin)
    id(Plugins.Serialization.plugin)
    id(Plugins.Parcelize.plugin)
    id(Plugins.Moko.plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

kotlin {
    androidTarget()

    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    cocoapods {
        version = "2.0.2"
        summary = "Compose application framework"
        homepage = "https://github.com/Radch-enko"
        ios.deploymentTarget = "12.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true

            export(Dependencies.Decompose.decompose)
            export(Dependencies.Essenty.essenty)
        }
        pod("GoogleSignIn")
    }
    targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosX64").compilations.forEach {
        it.kotlinOptions.freeCompilerArgs += arrayOf("-linker-options", "-lsqlite3")
    }
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xlazy-ir-for-caches=disable"
        }
    }
    kotlin.targets.removeAll {
        it.name == "iosArm32"
    }
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
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(Dependencies.ImageLoader.imageLoader)
                implementation(Dependencies.Napier.napier)
                implementation(Dependencies.KotlinxCoroutines.core)
                api(Dependencies.Ktor.Client.Core)
                api(Dependencies.Ktor.Client.CommonLogging)
                implementation(Dependencies.ComposeIcons.featherIcons)
                implementation(Dependencies.KotlinxSerialization.json)
                implementation(Dependencies.KotlinxDatetime.kotlinxDatetime)

                // MVI Kotlin
                api(Dependencies.MviKotlin.mviKotlin)
                api(Dependencies.MviKotlin.mviKotlinMain)
                api(Dependencies.MviKotlin.mviKotlinExtensionsCoroutines)

                // Decompose
                api(Dependencies.Decompose.decompose)
                api(Dependencies.Decompose.extensions)

                // Koin
                api(Dependencies.Koin.core)

                api(Dependencies.Essenty.essenty)

                //Moko
                api(Dependencies.Moko.resourcesCompose)

                implementation(Dependencies.Calendar.composeDatePicker)

                implementation(Dependencies.SqlDelight.primitiveadaper)

                implementation(project(":wheel-picker-compose"))
                implementation(project(":modal_custom_dialog"))
                implementation(project(":contract"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Dependencies.AndroidX.appCompat)
                implementation(Dependencies.AndroidX.activityCompose)
                implementation(Dependencies.Compose.uiTooling)
                implementation(Dependencies.KotlinxCoroutines.android)
                api(Dependencies.Ktor.Client.Android)
                implementation(Dependencies.Google.SignIn)
                implementation(Dependencies.AndroidX.activityKtx)
                implementation(Dependencies.SqlDelight.androidDriver)
                // Koin
                api(Dependencies.Koin.android)

                api(Dependencies.Ktor.Server.Logback)
            }
        }

        val iosX64Main by getting {
            resources.srcDirs("build/generated/moko/iosX64Main/src")
        }
        val iosArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosArm64Main/src")
        }
        val iosSimulatorArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosSimulatorArm64Main/src")
        }
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Dependencies.Ktor.Client.Darwin)
                //implementation(files("iosApp/GoogleAuthorization/GoogleAuthorization/Sources"))
                implementation(Dependencies.SqlDelight.nativeDriver)
            }
        }
    }
}

android {

    namespace = "band.effective.office.elevator"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 36

        applicationId = "band.effective.office.mobile"
        versionCode = 1
        versionName = "1.0.2"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("build/generated/moko/androidMain/src")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packagingOptions {
        resources.excludes.add("META-INF/**")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    signingConfigs {
        getByName("debug") {
            keyPassword = "android"
            storeFile = file("${rootDir}/keystore/debug.keystore")
            storePassword = "android"
        }
        create("release") {
            keyAlias = System.getenv()["OFFICE_ELEVATOR_RELEASE_ALIAS"]
            keyPassword = System.getenv()["OFFICE_ELEVATOR_RELEASE_KEY_PASSWORD"]
            storeFile = file("${rootDir}/keystore/effective-office.jks")
            storePassword = System.getenv()["OFFICE_ELEVATOR_RELEASE_STORE_PASSWORD"]
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isMinifyEnabled = false
        }
    }
}


multiplatformResources {
    resourcesPackage.set("band.effective.office.elevator")
    resourcesVisibility.set(Public)
    resourcesClassName.set("MainRes")
    iosBaseLocalizationRegion.set("ru") // optional, default "en"
}

buildConfig {
    className("OfficeElevatorConfig")
    packageName("band.effective.office.elevator")
    useKotlinOutput()
    useKotlinOutput { internalVisibility = true }
    buildConfigField(
        "String",
        "integrationPhoneId", "\"13c80c3d-4278-45cf-8d2a-e281004d3ff9\""
    )
    buildConfigField(
        "String",
        "integrationTelegramId", "\"15f1fa52-7656-4457-a1ce-42063b0f2b39\""
    )
    buildConfigField(
        "String",
        "webClient", "\"503255112190-4flfuu86ihrpismfl70nuae6u6n5gk4p.apps.googleusercontent.com\""
    )
    buildConfigField(
        "String",
        "iosClient",
        "\"503255112190-a3n1441gcnl7alamoqkvk9omtv5q97tl.apps.googleusercontent.com\""
    )
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("band.effective.office.elevator")
        }
    }
}

val podspec = tasks["podspec"] as org.jetbrains.kotlin.gradle.tasks.PodspecTask
podspec.doLast {
    val podspec = file("composeApp.podspec")
    val newPodspecContent = mutableListOf<String>()
    podspec.readLines().forEach {
        newPodspecContent.add(it)
        if (it.contains("set -ev")) {
            newPodspecContent.add("                export LANG=en_US.UTF-8")
        }
    }
    podspec.writeText(newPodspecContent.joinToString(separator = "\n"))
}
