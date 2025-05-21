plugins {
    id(Plugins.AndroidLib.plugin)
    id(Plugins.Kotlin.plugin)
}


android {
    namespace = "band.effective.office.tablet.features.network"
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
                // Koin
                api(Dependencies.Koin.core)

                implementation(project(":tabletApp:features:core"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Koin
                api(Dependencies.Koin.android)
            }
        }
    }
}
