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
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    //id("org.jetbrains.kotlin.plugin.serialization").apply(false)
//    alias(libs.plugins.buildConfig).apply(false)
}

allprojects {
    group = property("group").toString()
    version = property("version").toString()

    repositories {
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
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_17.toString()
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