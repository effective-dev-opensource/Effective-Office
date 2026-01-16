import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
}

configure<BaseExtension> {
    val localProperties = gradleLocalProperties(rootDir, providers)
    val releaseKeyAlias = localProperties.getProperty("sign.keyAlias", null)
        ?: System.getenv()["OFFICE_ELEVATOR_RELEASE_ALIAS"]
    val releaseKeyPassword = localProperties.getProperty("sign.keyPassword", null)
        ?: System.getenv()["OFFICE_ELEVATOR_RELEASE_KEY_PASSWORD"]
    val releaseStorePassword = localProperties.getProperty("sign.storePassword", null)
        ?: System.getenv()["OFFICE_ELEVATOR_RELEASE_STORE_PASSWORD"]
    signingConfigs {
        getByName("debug") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storeFile = file("${rootDir}/keystore/debug.keystore")
            storePassword = "android"
        }
        create("release") {
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
            storeFile = file("${rootDir}/keystore/main.keystore")
            storePassword = releaseStorePassword
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
