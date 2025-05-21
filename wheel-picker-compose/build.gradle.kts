import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(Plugins.Kotlin.plugin)
    id(Plugins.MultiplatformCompose.plugin)
    id(Plugins.AndroidLib.plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

android {
    namespace = "com.commandiron.wheel_picker_composer"
    compileSdk = 33
    defaultConfig {
        minSdk = ConfigData.Android.minSdkVersion
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)

                implementation(Dependencies.KotlinxDatetime.kotlinxDatetime)
            }
        }
        val androidMain by getting
    }
}
