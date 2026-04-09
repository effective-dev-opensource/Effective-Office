import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.multiplatform)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.buildkonfig)
    id("sign-config")
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.bundles.koin)
                implementation(libs.bundles.ktor.client)
                implementation(libs.decompose)
                implementation(libs.decompose.compose.jetbrains)
                implementation(libs.essenty.lifecycle)
                implementation(libs.essenty.state.keeper)
                implementation(libs.napier)

                implementation(project(":clients:shared:core"))
                implementation(project(":clients:tv:core:domain"))
                implementation(project(":clients:tv:core:data"))
                implementation(project(":clients:tv:core:ui"))
                implementation(project(":clients:tv:feature:menu"))
                implementation(project(":clients:tv:feature:photos"))
                implementation(project(":clients:tv:feature:stories"))
                implementation(project(":clients:tv:feature:events"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.uiTooling)
                implementation(libs.androidx.activityCompose)
                implementation(libs.koin.android)

                implementation(project(":clients:tv:feature:selfUpdate"))
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "band.effective.office.tv"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        applicationId = "band.effective.office.tv"
        versionCode = 2
        versionName = "2.1.0"

        buildFeatures {
            buildConfig = true
        }

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".dev"
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/INDEX.LIST",
            )
        }
    }
}

val apiUrlRelease: String = gradleLocalProperties(rootDir, providers).getProperty("api.url.release", "")
val apiUrlDebug: String = gradleLocalProperties(rootDir, providers).getProperty("api.url.debug", "")
val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("apiKey", "")
val appVersionName: String = android.defaultConfig.versionName!!
val tvIsDebug: String = gradleLocalProperties(rootDir, providers).getProperty("tv.isDebug", "false")
val crashHandlerBotToken: String = gradleLocalProperties(rootDir, providers).getProperty("crashHandler.botToken", "")
val crashHandlerChatId: String = gradleLocalProperties(rootDir, providers).getProperty("crashHandler.chatId", "")
val selfUpdateApiUrl: String = "https://d5dpp3puo2m6ma9t17h3.apigw.yandexcloud.net/actualApk"

compose.desktop {
    application {
        mainClass = "band.effective.office.tv.DesktopAppKt"
        
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "Effective Office TV"
            packageVersion = appVersionName
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "band.effective.office.tv"
    generateResClass = auto
}

buildkonfig {
    packageName = "band.effective.office.tv"
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
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            "IS_DEBUG",
            tvIsDebug,
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "CRASH_HANDLER_BOT_TOKEN",
            crashHandlerBotToken,
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "CRASH_HANDLER_CHAT_ID",
            crashHandlerChatId,
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SELF_UPDATE_API_URL",
            selfUpdateApiUrl,
        )
    }
}

