import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    configurations.all {
        resolutionStrategy {
            force("org.apache.commons:commons-compress:1.27.1")
        }
    }
}
plugins {
    alias(libs.plugins.multiplatform).apply(false)
    // Без версии: её биндит settings.gradle.kts по варианту сборки (upstream 1.10.2 / форк 0.0.4-aurora).
    id("org.jetbrains.compose").apply(false)
    alias(libs.plugins.android.application).apply(false)
    //id("org.jetbrains.kotlin.plugin.serialization").apply(false)
//    alias(libs.plugins.buildConfig).apply(false)
}

allprojects {
    group = property("group").toString()
    version = property("version").toString()

    repositories {
        // Репозитории проекта перебивают заданные в settings.gradle.kts (режим PREFER_PROJECT),
        // поэтому локальный maven-форк под Аврору дублируем и здесь.
        java.util.Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")?.let {
            maven(url = rootProject.file(it).canonicalFile.toURI())
        }
        mavenCentral()
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://androidx.dev/storage/compose-compiler/repository")
        maven(url = "https://jitpack.io")
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    // kotlinx-datetime 0.7.x is built on the still-experimental kotlin.time.Clock/Instant,
    // so opt in project-wide for every Kotlin compilation (JVM, Android and Native).
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // Ensure feature module jars under backend have unique names
    if (project.path.startsWith(":backend:feature") || project.projectDir.path.contains("${File.separator}backend${File.separator}feature")) {
        tasks.withType<org.gradle.jvm.tasks.Jar> {
            val normalized = project.path.trimStart(':').replace(':', '-')
            archiveBaseName.set(normalized)
        }
    }
}
allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.apache.commons:commons-compress:1.24.0")
        }
    }
}