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
        // Get deployment parameters
        val remoteUser = project.findProperty("remoteUser") ?: "remoteUser"
        val remoteHost = project.findProperty("remoteHost") ?: "remoteHost"
        val remotePath = project.findProperty("remotePath") ?: "remotePath"
        val jarVersion = project.version.toString()

        // Create deploy directory if it doesn't exist
        mkdir("${rootProject.projectDir}/deploy/prod")

        // Copy the JAR file to the deploy folder and rename it
        copy {
            from("${project.buildDir}/libs/app-${jarVersion}-boot.jar")
            into("${rootProject.projectDir}/deploy/prod")
        }

        // Copy the deploy directory to the remote server
        exec {
            commandLine("scp", "-r", "${rootProject.projectDir}/deploy/prod", "${remoteUser}@${remoteHost}:${remotePath}")
        }

        println("Production deployment completed successfully.")
    }
}

tasks.register("deployDev") {
    group = "deployment"
    description = "Deploys the application to dev environment"

    dependsOn("bootJar")

    doLast {
        // Get deployment parameters
        val remoteUser = project.findProperty("remoteUser") ?: "remoteUser"
        val remoteHost = project.findProperty("remoteHost") ?: "remoteHost"
        val remotePath = project.findProperty("remotePath") ?: "remotePath"
        val jarVersion = project.version.toString()

        // Create deploy directory if it doesn't exist
        mkdir("${rootProject.projectDir}/deploy/dev")

        // Copy the JAR file to the deploy folder and rename it
        copy {
            from("${project.buildDir}/libs/app-${jarVersion}-boot.jar")
            into("${rootProject.projectDir}/deploy/dev")
            rename { "app-${jarVersion}-dev-boot.jar" }
        }

        // Copy the deploy directory to the remote server
        exec {
            commandLine("scp", "-r", "${rootProject.projectDir}/deploy/dev", "${remoteUser}@${remoteHost}:${remotePath}")
        }

        println("Development deployment completed successfully.")
    }
}

