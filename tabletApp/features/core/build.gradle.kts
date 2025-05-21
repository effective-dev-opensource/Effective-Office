plugins {
    id(Plugins.MultiplatformCompose.plugin)
    id(Plugins.AndroidLib.plugin)
    id(Plugins.Kotlin.plugin)
    id(Plugins.Libres.plugin)
    id(Plugins.Parcelize.plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

android {
    compileSdk = 34
    namespace = "band.effective.office.tablet.features.core"
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
                implementation(compose.material)
                implementation(compose.material3)

                // Decompose
                implementation(Dependencies.Decompose.decompose)
                implementation(Dependencies.Decompose.extensions)

                //Libres
                implementation(Dependencies.Libres.libresCompose)

                //EpicDatePicker
                implementation(Dependencies.Calendar.composeDatePicker)

                // MVI Kotlin
                api(Dependencies.MviKotlin.mviKotlin)
                api(Dependencies.MviKotlin.mviKotlinMain)
                api(Dependencies.MviKotlin.mviKotlinExtensionsCoroutines)

                //Koin
                api(Dependencies.Koin.core)

                //WheelTimePicker
                implementation(Dependencies.KotlinxDatetime.kotlinxDatetime)
                implementation(project(":wheel-picker-compose"))

                api(project(":contract"))
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
