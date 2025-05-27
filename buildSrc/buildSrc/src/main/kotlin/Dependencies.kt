object Dependencies {

    object Versions {
        const val activityKtx = "1.7.2"
        const val kotlin = "2.1.21"
        const val agp = "7.4.2"
        const val compose = "1.4.0"
        const val androidxAppCompat = "1.6.1"
        const val androidxActivityCompose = "1.7.2"
        const val composeUiTooling = "1.4.3"
        const val libres = "1.1.8"
        const val composeImageLoader = "1.6.0"
        const val napier = "2.6.1"
        const val buildConfig = "3.1.0"
        const val kotlinxCoroutines = "1.7.1"
        const val kotlinCorutinesMultiplatform = "1.6.1-native-mt"
        const val ktor = "2.3.2"
        const val composeIcons = "1.1.0"
        const val kotlinxSerialization = "1.5.1"
        const val kotlinxDatetime = "0.4.0"
        const val googleAuth = "20.5.0"
        const val decompose = "2.0.0-compose-experimental-beta-01"
        const val mviKotlin = "3.2.1"
        const val koin = "3.4.0"
        const val essenty = "1.1.0"
        const val logback = "1.2.11"
        const val shadow = "7.1.2"
        const val moko = "0.24.5"
        const val calendar = "1.0.8"
        const val sqlDelight = "2.0.0-rc02"
    }

    object Ktor {
        const val ktorVersion = "2.3.1"

        object Client {
            const val Core =
                "io.ktor:ktor-client-core:$ktorVersion"
            const val CommonLogging =
                "io.ktor:ktor-client-logging:$ktorVersion"
            const val CIO = "io.ktor:ktor-client-cio:$ktorVersion"
            const val Android = "io.ktor:ktor-client-android:${Versions.ktor}"
            const val Darwin = "io.ktor:ktor-client-darwin:${Versions.ktor}"
            const val Auth = "io.ktor:ktor-client-auth:$ktorVersion"
            const val negotiation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"
            const val jsonSerialization = "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
            const val logging = "io.ktor:ktor-client-logging:$ktorVersion"
        }

        object Server {
            const val Netty = "io.ktor:ktor-server-netty-jvm:$ktorVersion"
            const val Logback = "ch.qos.logback:logback-classic:1.2.11"
        }
    }


    object Google {
        private const val VERSION = "20.4.0"
        const val SignIn = "com.google.android.gms:play-services-auth:$VERSION"
        const val ApiClient = "com.google.api-client:google-api-client:2.1.1"
    }


    object AndroidX {
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
        const val activityCompose =
            "androidx.activity:activity-compose:${Versions.androidxActivityCompose}"
    }

    object Compose {
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.composeUiTooling}"
    }

    object Libres {
        const val libresCompose = "io.github.skeptick.libres:libres-compose:${Versions.libres}"
    }

    object ImageLoader {
        const val imageLoader = "io.github.qdsfdhvh:image-loader:${Versions.composeImageLoader}"
    }

    object Napier {
        const val napier = "io.github.aakira:napier:${Versions.napier}"
    }

    object KotlinxCoroutines {
        const val core =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}"
        const val android =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinxCoroutines}"
    }

    object ComposeIcons {
        const val featherIcons = "br.com.devsrsouza.compose.icons:feather:${Versions.composeIcons}"
    }

    object KotlinxSerialization {
        const val json =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
    }

    object KotlinxDatetime {
        const val kotlinxDatetime =
            "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinxDatetime}"
    }

    object GoogleAuth {
        const val googleAuth = "com.google.android.gms:play-services-auth:${Versions.googleAuth}"
    }

    object Decompose {
        const val decompose = "com.arkivanov.decompose:decompose:${Versions.decompose}"
        const val extensions =
            "com.arkivanov.decompose:extensions-compose-jetbrains:${Versions.decompose}"
    }

    object MviKotlin {
        const val mviKotlin = "com.arkivanov.mvikotlin:mvikotlin:${Versions.mviKotlin}"
        const val mviKotlinMain = "com.arkivanov.mvikotlin:mvikotlin-main:${Versions.mviKotlin}"
        const val mviKotlinExtensionsCoroutines =
            "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:${Versions.mviKotlin}"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object Essenty {
        const val essenty = "com.arkivanov.essenty:lifecycle:${Versions.essenty}"
    }

    object Moko{
        const val resourcesCompose = "dev.icerock.moko:resources-compose:${Versions.moko}"
    }

    object Calendar{
        const val composeBasic = "io.github.epicarchitect:calendar-compose-basis:${Versions.calendar}"
        const val composeRanges = "io.github.epicarchitect:calendar-compose-ranges:${Versions.calendar}"
        const val composePager = "io.github.epicarchitect:calendar-compose-pager:${Versions.calendar}"
        const val composeDatePicker = "io.github.epicarchitect:epic-calendar-compose:${Versions.calendar}"
    }

    object SqlDelight {
        const val nativeDriver = "app.cash.sqldelight:native-driver:${Versions.sqlDelight}"
        const val androidDriver = "app.cash.sqldelight:android-driver:${Versions.sqlDelight}"
        const val primitiveadaper = "app.cash.sqldelight:primitive-adapters:${Versions.sqlDelight}"
        const val coroutinesExtensions = "app.cash.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
    }
}