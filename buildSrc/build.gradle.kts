plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    maven(url = "https://dl.bintray.com/icerockdev/moko")
    jcenter()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(Plugins.Android.implementation)
    implementation(Plugins.Shadow.implementation) // NOTE(radchenko): generates jar for server builds
    implementation(Plugins.Kotlin.implementation)
    implementation(Plugins.Ktor.implementation)
    implementation(Plugins.ApacheCompress.implementation) // NOTE(radchenko): needs for `ktor` to `docker`
    implementation(Plugins.MultiplatformCompose.implementation)
    implementation(Plugins.CocoaPods.implementation)
    implementation(Plugins.BuildConfig.implementation)
    implementation(Plugins.Serialization.implementation)
    implementation(Plugins.Moko.implementation)
    implementation(Plugins.Libres.implementation)
    implementation(Plugins.GoogleServices.implementation) //NOTE(Maksim Mishenko): need for firebase message
}

val rootDirProject = file("../")

kotlin {
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}