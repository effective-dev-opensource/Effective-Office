plugins {
    id(Plugins.AndroidLib.plugin)
    id(Plugins.MultiplatformCompose.plugin)
    id(Plugins.Kotlin.plugin)
    id(Plugins.Parcelize.plugin)
    id(Plugins.Libres.plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

android {
    namespace = "band.effective.office.tablet.features.roomInfo"
    compileSdk = 33
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        res.srcDir("build/generated/libres/android/resources")
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
                implementation(compose.material3)



                // Decompose
                implementation(Dependencies.Decompose.decompose)
                implementation(Dependencies.Decompose.extensions)

                // Koin
                api(Dependencies.Koin.core)

                //Libres
                implementation(Dependencies.Libres.libresCompose)

                // MVI Kotlin
                api(Dependencies.MviKotlin.mviKotlin)
                api(Dependencies.MviKotlin.mviKotlinMain)
                api(Dependencies.MviKotlin.mviKotlinExtensionsCoroutines)

                //EpicDatePicker
                implementation(Dependencies.Calendar.composeDatePicker)

                //WheelTimePicker
                implementation(Dependencies.KotlinxDatetime.kotlinxDatetime)
                implementation(project(":wheel-picker-compose"))

                implementation(project(":tabletApp:features:core"))
                implementation(project(":tabletApp:features:network"))
                implementation(project(":tabletApp:features:domain"))
                implementation(project(":tabletApp:features:selectRoom"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Koin
                api(Dependencies.Koin.android)

                implementation(project(":tabletApp:features:selectRoom"))
            }
        }
    }
}

libres {
    // https://github.com/Skeptick/libres#setup
    generatedClassName = "MainRes" // "Res" by default
    generateNamedArguments = true // false by default
    baseLocaleLanguageCode = "ru" // "en" by default
    camelCaseNamesForAppleFramework = true // false by default

}
