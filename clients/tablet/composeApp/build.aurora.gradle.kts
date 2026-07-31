import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.compose")
    alias(libs.plugins.kotlinx.serialization)
    id("com.codingfeline.buildkonfig")
    id("ru.auroraos.kmp.aurora-build")
    id("ru.auroraos.kmp.aurora-devices")
}

kotlin {
    listOf(
        linuxArm64(),
        linuxX64(),
    ).forEach { target ->
        target.binaries {
            executable {
                entryPoint = "band.effective.office.tablet.main"
                freeCompilerArgs += auroraBuild.freeCompilerArgs(target.name)
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
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

auroraBuild {
    rpm {
        id.set("band.effective.office.tablet")
        name.set("Effective Office")
        description.set("Meeting room tablet built with KMP for Aurora OS")
        version.set("1.0.1")
        permissions.set(listOf("Internet"))
        libs3rdParty.set(listOf("maliit-glib"))
        icons.set(projectDir.toPath().resolve("icons"))
    }
}

val auroraDeviceIp: String = (project.findProperty("AURORA_DEVICE_IP") as? String)
    ?: localProperties.getProperty("AURORA_DEVICE_IP")
    ?: "192.168.0.22"

auroraDevices {
    devices {
        create {
            host.set(auroraDeviceIp)
            user.set("defaultuser")
            port.set(22)
            sshKey.set(File(System.getProperty("user.home")).resolve(".ssh/qtc_id").toPath())
        }
    }
    packages {
        create("release") {
            targets.set(listOf("aarch64", "x86_64"))
            directory.set(
                layout.buildDirectory.dir("rpm/release/{target}/RPMS/{target}").get().asFile.toPath(),
            )
            mask.set("""(?!.*debug).*\.rpm""")
        }
    }
}

// Aurora packages resources FLAT and without a namespace: <qualifier>/<file> becomes
// <qualifier>_<file>, so any module's Res finds a file by name alone (which is why the string
// file names are split per module). Collect the composeResources of every tablet module into
// one directory: aurora-build packages preparedResources of its own module only.
val auroraResourceModules = listOf(
    "clients/tablet/core/ui",
    "clients/tablet/feature/main",
    "clients/tablet/feature/settings",
    "clients/tablet/feature/bookingEditor",
    "clients/tablet/feature/fastbooking",
    "clients/tablet/feature/slot",
)

val stageAuroraResources by tasks.registering(Copy::class) {
    description = "Собирает composeResources всех модулей планшета в один каталог для упаковки в RPM."
    duplicatesStrategy = DuplicatesStrategy.FAIL
    into(layout.buildDirectory.dir("auroraComposeResources"))
    from(layout.projectDirectory.dir("src/commonMain/composeResources"))
    auroraResourceModules.forEach { module ->
        from(rootProject.layout.projectDirectory.dir("$module/src/commonMain/composeResources"))
    }
}

compose.resources {
    generateResClass = never
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = stageAuroraResources.map { layout.buildDirectory.dir("auroraComposeResources").get() },
    )
}

buildkonfig {
    packageName = "band.effective.office.tablet"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", "1.0.1")
        buildConfigField(FieldSpec.Type.STRING, "API_URL_RELEASE", localProperties.getProperty("api.url.release"))
        buildConfigField(FieldSpec.Type.STRING, "API_URL_DEBUG", localProperties.getProperty("api.url.debug"))
        buildConfigField(FieldSpec.Type.STRING, "API_KEY", localProperties.getProperty("apiKey"))
    }
}

apply(from = "aurora-tasks.gradle.kts")
