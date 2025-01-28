val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val ktorm_version: String by project
val postgresql_driver_version: String by project
val snakeyaml_version: String by project
val liquibase_version: String by project
val mockito_version: String by project

plugins {
    kotlin("jvm") version "1.8.22"
    id("io.ktor.plugin") version "2.3.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.22"
    id("org.liquibase.gradle") version "2.2.0"
    id("org.jetbrains.dokka") version "1.8.20"
}

group = "office.effective"
version = "0.0.1"
application {
    mainClass.set("office.effective.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    fatJar {
        archiveFileName.set("backendApp.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("io.ktor:ktor-server-request-validation:$ktor_version")
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.ktorm:ktorm-support-postgresql:$ktorm_version")
    implementation("org.postgresql:postgresql:$postgresql_driver_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.2")
    implementation("io.ktor:ktor-client-core-jvm:2.3.2")
    implementation("io.ktor:ktor-client-apache-jvm:2.3.2")
    implementation("org.liquibase:liquibase-core:$liquibase_version")
    implementation("io.ktor:ktor-client-apache:2.3.2")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("io.github.smiley4:ktor-swagger-ui:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.32.1")
    implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.3.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.firebase:firebase-admin:8.2.0")
    implementation("com.google.guava:guava:33.4.0-jre")

    liquibaseRuntime("org.liquibase:liquibase-core:$liquibase_version")
    liquibaseRuntime("org.postgresql:postgresql:$postgresql_driver_version")
    liquibaseRuntime("org.yaml:snakeyaml:$snakeyaml_version")
    liquibaseRuntime("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.mockito:mockito-core:$mockito_version")
    testImplementation("org.mockito:mockito-inline:$mockito_version")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockito_version")
    testImplementation("com.h2database:h2:2.2.220")
    implementation(kotlin("stdlib"))
}
