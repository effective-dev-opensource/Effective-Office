rootProject.name = "effective-office"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        val forkPath = java.util.Properties().apply {
            val file = rootDir.resolve("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (forkPath != null) maven(url = rootDir.resolve(forkPath).canonicalFile.toURI()) else mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        if (providers.gradleProperty("buildVariant").orNull == "aurora") {
            id("org.jetbrains.compose") version "0.0.4-aurora"
            id("ru.auroraos.kmp.aurora-build") version "0.0.1"
            id("ru.auroraos.kmp.aurora-devices") version "0.0.1"
            id("com.codingfeline.buildkonfig") version "0.18.0-aurora"
        } else {
            id("org.jetbrains.compose") version "1.10.2"
        }
    }
}

dependencyResolutionManagement {
    repositories {
        val forkPath = java.util.Properties().apply {
            val file = rootDir.resolve("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (forkPath != null) maven(url = rootDir.resolve(forkPath).canonicalFile.toURI()) else mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://androidx.dev/storage/compose-compiler/repository")
        maven(url = "https://jitpack.io")
    }

}
plugins {
    //https://github.com/JetBrains/compose-hot-reload?tab=readme-ov-file#set-up-automatic-provisioning-of-the-jetbrains-runtime-jbr-via-gradle
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}

val isAuroraVariant = providers.gradleProperty("buildVariant").orNull == "aurora"

if (isAuroraVariant) {
    val auroraModules = listOf(
        "clients:shared:core",
        "clients:tablet:composeApp",
        "clients:tablet:core:ui",
        "clients:tablet:core:domain",
        "clients:tablet:core:data",
        "clients:tablet:feature:main",
        "clients:tablet:feature:settings",
        "clients:tablet:feature:bookingEditor",
        "clients:tablet:feature:fastBooking",
        "clients:tablet:feature:slot",
    )
    include(auroraModules)

    project(":clients:tablet:feature:fastBooking").projectDir = file("clients/tablet/feature/fastbooking")

    auroraModules.forEach { project(":$it").buildFileName = "build.aurora.gradle.kts" }
} else {

include(":backend")
include(
    "backend:app",
    "backend:core:domain",
    "backend:core:repository",
    "backend:core:data",
    "backend:feature",
    "backend:feature:authorization",
    "backend:feature:user",
    "backend:feature:booking:core",
    "backend:feature:booking:calendar:google",
    "backend:feature:booking:calendar:dummy",
    "backend:feature:workspace",
    "backend:feature:calendar-subscription",
    "backend:feature:notifications",
    "backend:feature:duolingo",
    "backend:feature:leader-id",
    "backend:feature:photos:core",
    "backend:feature:photos:provider:synology",
    "backend:feature:photos:provider:dummy",
    "backend:feature:sport:core",
    "backend:feature:sport:provider:dummy",
    "backend:feature:sport:provider:clockify",
    "backend:feature:teammates:core",
    "backend:feature:teammates:provider:notion",
    "backend:feature:teammates:provider:dummy",
    "backend:feature:photo-saver:core",
    "backend:feature:photo-saver:provider:dummy",
    "backend:feature:photo-saver:provider:mattermost",
    "backend:feature:photo-saver:storage:synology",
    "backend:feature:photo-saver:storage:dummy",

    "clients:tablet:composeApp",
    "clients:tablet:core:ui",
    "clients:tablet:core:domain",
    "clients:tablet:core:data",
    "clients:tablet:feature:main",
    "clients:tablet:feature:settings",
    "clients:tablet:feature:bookingEditor",
    "clients:tablet:feature:fastBooking",
    "clients:tablet:feature:slot",

    "clients:shared:core",

    "clients:tv:composeApp",
    "clients:tv:core:domain",
    "clients:tv:core:data",
    "clients:tv:core:ui",
    "clients:tv:feature:menu",
    "clients:tv:feature:photos",
    "clients:tv:feature:stories",
    "clients:tv:feature:events",
    "clients:tv:feature:selfUpdate",

    "clients:smsrouter:app",
)

}
