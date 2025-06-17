import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(Plugins.Kotlin.plugin)
    id(Plugins.MultiplatformCompose.plugin)
    id(Plugins.AndroidLib.plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}
android {
    namespace = "band.effective.office.dialog"
    compileSdk = 33
    defaultConfig {
        minSdk = ConfigData.Android.minSdkVersion
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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.animation)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}