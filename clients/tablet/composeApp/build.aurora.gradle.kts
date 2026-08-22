import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

// See clients/shared/core/build.aurora.gradle.kts for why the convention plugins are avoided.
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.compose")
    alias(libs.plugins.kotlinx.serialization)
    id("com.codingfeline.buildkonfig")
}

kotlin {
    linuxArm64()
    linuxX64()

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
