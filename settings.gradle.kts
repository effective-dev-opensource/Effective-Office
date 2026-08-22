rootProject.name = "effective-office"

// The Aurora fork is a local maven outside git; its path lives in local.properties under
// auroraMavenPath. pluginManagement and dependencyResolutionManagement are compiled apart from the
// script body and cannot see its declarations, so the lookup is repeated inside each of them. The
// same path appears a third time in the root build.gradle.kts, where project repositories win over
// the ones declared here.

pluginManagement {
    includeBuild("build-logic")
    repositories {
        val forkPath = java.util.Properties().apply {
            val file = rootDir.resolve("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (forkPath != null) maven(url = rootDir.resolve(forkPath).canonicalFile.toURI())
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    // The Compose version is bound here and nowhere else, so that build files can ask for
    // `id("org.jetbrains.compose")` without one. It has to be pluginManagement: a top-level
    // `plugins {}` block of a settings script applies to the Settings object, which really applies
    // Compose and breaks configuration.
    plugins {
        if (providers.gradleProperty("buildVariant").orNull == "aurora") {
            id("org.jetbrains.compose") version "0.0.4-aurora"
            id("ru.auroraos.kmp.aurora-build") version "0.0.1"
            id("ru.auroraos.kmp.aurora-devices") version "0.0.1"
            id("com.codingfeline.buildkonfig") version "0.18.0-aurora"
        } else {
            // Read rather than repeated: the tablet and tv modules still ask for this plugin
            // through `alias(libs.plugins.compose)`, and one invocation cannot resolve two
            // versions of the same plugin id.
            id("org.jetbrains.compose") version rootDir.resolve("gradle/libs.versions.toml")
                .readLines().first { it.startsWith("compose = ") }
                .substringAfter('"').substringBefore('"')
        }
    }
}

dependencyResolutionManagement {
    repositories {
        val forkPath = java.util.Properties().apply {
            val file = rootDir.resolve("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (forkPath != null) maven(url = rootDir.resolve(forkPath).canonicalFile.toURI())
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


if (providers.gradleProperty("buildVariant").orNull == "aurora") {
    // Only the tablet is configured under Aurora: backend, tv and smsrouter pull AGP and upstream
    // Compose in through build-logic, and the two Compose plugins do not share an invocation.
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

    // The project path stays as upstream spells it, because other modules depend on it by that
    // path, while the directory on disk is `fastbooking` — naming it keeps the build off a
    // case-insensitive filesystem.
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
