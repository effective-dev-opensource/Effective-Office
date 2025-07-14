plugins {
    id("band.effective.office.backend.spring-boot-application")
}

dependencies {
    implementation(project(":backend:core:data"))
    implementation(project(":backend:core:domain"))
    implementation(project(":backend:core:repository"))
    implementation(project(":backend:feature:user"))
    implementation(project(":backend:feature:booking:core"))
    implementation(project(":backend:feature:booking:calendar:google"))
    implementation(project(":backend:feature:booking:calendar:dummy"))
    implementation(project(":backend:feature:workspace"))
    implementation(project(":backend:feature:authorization"))
    implementation(project(":backend:feature:calendar-subscription"))
    implementation(project(":backend:feature:notifications"))

    implementation("org.springframework:spring-tx")
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // .env file support
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
}

// Deployment tasks
// Usage:
// ./gradlew deployProd -PremoteUser=user -PremoteHost=hostname -PremotePath=/path/to/deploy
// ./gradlew deployDev -PremoteUser=user -PremoteHost=hostname -PremotePath=/path/to/deploy
//
// Parameters:
// - remoteUser: SSH username for the remote server
// - remoteHost: Hostname or IP address of the remote server
// - remotePath: Path on the remote server where the application will be deployed
//
// The deployment tasks will:
// 1. Build the application JAR (with -dev suffix for development environment)
// 2. Copy the JAR to the deploy directory and rename it to boot.jar
// 3. Copy the deploy directory to the remote server
tasks.register("deployProd") {
    group = "deployment"
    description = "Deploys the application to production environment"

    dependsOn("bootJar")

    doLast {
        println("🚀 Starting production deployment process...")

        val remoteUser = project.findProperty("remoteUser") ?: "remoteUser"
        val remoteHost = project.findProperty("remoteHost") ?: "remoteHost"
        val remotePath = project.findProperty("remotePath") ?: "remotePath"
        val jarVersion = project.version.toString()

        println("📦 Deployment parameters:")
        println("  remoteUser=$remoteUser")
        println("  remoteHost=$remoteHost")
        println("  remotePath=$remotePath")
        println("  jarVersion=$jarVersion")

        val deployDir = file("${rootProject.projectDir}/deploy/prod")
        mkdir(deployDir)

        println("🛠️ Preparing JAR file...")
        copy {
            from("${project.buildDir}/libs/app-${jarVersion}-boot.jar")
            into(deployDir)
            rename { "app-${jarVersion}-boot.jar" }
        }

        println("🧹 Cleaning remote JARs...")
        exec {
            commandLine("ssh", "$remoteUser@$remoteHost", "rm -rf $remotePath/prod")
        }

        println("📤 Copying deploy directory to remote server (rsync)...")
        val rsyncCommand = listOf(
            "rsync", "-az", "--progress",
            "${deployDir.absolutePath}/",
            "$remoteUser@$remoteHost:$remotePath/prod/"
        )

        val process = ProcessBuilder(rsyncCommand)
            .inheritIO()
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw GradleException("❌ rsync failed with exit code $exitCode")
        }

        println("✅ Production deployment completed successfully.")
    }
}

tasks.register("deployDev") {
    group = "deployment"
    description = "Deploys the application to dev environment"

    dependsOn("bootJar")

    doLast {
        println("🚀 Starting development deployment process...")

        val remoteUser = project.findProperty("remoteUser") ?: "remoteUser"
        val remoteHost = project.findProperty("remoteHost") ?: "remoteHost"
        val remotePath = project.findProperty("remotePath") ?: "remotePath"
        val jarVersion = project.version.toString()

        println("📦 Deployment parameters:")
        println("  remoteUser=$remoteUser")
        println("  remoteHost=$remoteHost")
        println("  remotePath=$remotePath")
        println("  jarVersion=$jarVersion")

        val deployDir = file("${rootProject.projectDir}/deploy/dev")
        mkdir(deployDir)

        println("🛠️ Preparing JAR file...")
        copy {
            from("${project.buildDir}/libs/app-${jarVersion}-boot.jar")
            into(deployDir)
            rename { "app-${jarVersion}-dev-boot.jar" }
        }

        println("🧹 Cleaning remote JARs...")
        exec {
            commandLine("ssh", "$remoteUser@$remoteHost", "rm -rf $remotePath/dev")
        }

        println("📤 Copying deploy directory to remote server (rsync)...")
        val rsyncCommand = listOf(
            "rsync", "-az", "--progress",
            "${deployDir.absolutePath}/",
            "$remoteUser@$remoteHost:$remotePath/dev/"
        )

        val process = ProcessBuilder(rsyncCommand)
            .inheritIO()
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw GradleException("❌ rsync failed with exit code $exitCode")
        }

        println("✅ Deployment completed successfully.")
    }
}
