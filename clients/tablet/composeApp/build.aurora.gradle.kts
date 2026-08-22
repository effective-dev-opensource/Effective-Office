import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

// See clients/shared/core/build.aurora.gradle.kts for why the convention plugins are avoided.
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.compose")
    alias(libs.plugins.kotlinx.serialization)
    id("com.codingfeline.buildkonfig")
    id("ru.auroraos.kmp.aurora-build")
}

kotlin {
    listOf(
        linuxArm64(),
        linuxX64(),
    ).forEach { target ->
        target.binaries {
            executable {
                entryPoint = "band.effective.office.tablet.main"
                // -Xoverride-konan-properties, without which the link against the sysroot fails.
                freeCompilerArgs += auroraBuild.freeCompilerArgs(target.name)
                // cmpLinkerOpts brings Qt5Core, maliit, skiko, wayland, EGL and dbus, but no
                // networking, and the http client is ktor-curl.
                linkerOpts.addAll(auroraBuild.cmpLinkerOpts(target.name, "Qt5Network"))
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            api(libs.aurora.kotlinx.datetime)

            implementation(compose.navigation)
            implementation(libs.aurora.lifecycle.viewmodel)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlin.coroutines.core)

            implementation(libs.bundles.aurora.koin)

            implementation(project(":clients:tablet:core:ui"))
            implementation(project(":clients:tablet:feature:main"))
            implementation(project(":clients:tablet:feature:settings"))
            implementation(project(":clients:tablet:feature:bookingEditor"))
            implementation(project(":clients:tablet:feature:fastBooking"))
            implementation(project(":clients:tablet:feature:slot"))

            implementation(project(":clients:tablet:core:data"))
            implementation(project(":clients:tablet:core:domain"))
        }

        linuxMain.dependencies {
            // Backs SettingsStore: multiplatform-settings has no linux target.
            implementation(libs.aurora.ak.shared.preferences)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

val composeResourcesPath = "src/commonMain/composeResources"
val auroraComposeResourcesDir = layout.buildDirectory.dir("auroraComposeResources")

val auroraResourceModules = listOf(
    "clients/tablet/core/ui",
    "clients/tablet/feature/main",
    "clients/tablet/feature/settings",
    "clients/tablet/feature/bookingEditor",
)

// aurora-build packages the preparedResources of this module alone and does not walk the project
// dependencies, so every module's resources are gathered here first. A name clash has to fail the
// build — see "Resource packaging on Aurora" in README.md.
val stageAuroraResources by tasks.registering(Copy::class) {
    description = "Gathers the composeResources of every tablet module into one directory."
    duplicatesStrategy = DuplicatesStrategy.FAIL
    into(auroraComposeResourcesDir)
    from(layout.projectDirectory.dir(composeResourcesPath))
    auroraResourceModules.forEach { module ->
        from(rootProject.layout.projectDirectory.dir("$module/$composeResourcesPath"))
    }
}

compose.resources {
    // core:ui and feature/* generate the Res classes; this module only packages what they carry.
    generateResClass = never
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = stageAuroraResources.map { auroraComposeResourcesDir.get() },
    )
}

// AGP is not applied under Aurora, so gradleLocalProperties() is out of reach.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

buildkonfig {
    packageName = "band.effective.office.tablet"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", version.toString())
        buildConfigField(FieldSpec.Type.STRING, "API_URL_RELEASE", localProperties.getProperty("api.url.release"))
        buildConfigField(FieldSpec.Type.STRING, "API_URL_DEBUG", localProperties.getProperty("api.url.debug"))
        buildConfigField(FieldSpec.Type.STRING, "API_KEY", localProperties.getProperty("apiKey"))
    }
}
