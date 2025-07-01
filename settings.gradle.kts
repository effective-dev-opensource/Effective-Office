rootProject.name = "effective-office"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://androidx.dev/storage/compose-compiler/repository")
    }

}
plugins {
    //https://github.com/JetBrains/compose-hot-reload?tab=readme-ov-file#set-up-automatic-provisioning-of-the-jetbrains-runtime-jbr-via-gradle
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}


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

    "clients:tablet:composeApp",
    "clients:tablet:core:ui",
    "clients:tablet:core:domain",
    "clients:tablet:core:data",
    "clients:tablet:feature:main",
    "clients:tablet:feature:settings",
)
