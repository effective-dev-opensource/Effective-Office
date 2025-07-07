plugins {
    id("band.effective.office.backend.spring-boot-common")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":backend:core:domain"))
    implementation(project(":backend:core:data"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

// This ensures the jar is not executable
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

// This ensures the jar is created
tasks.getByName<Jar>("jar") {
    enabled = true
}

// Configure JPA plugin for Kotlin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
