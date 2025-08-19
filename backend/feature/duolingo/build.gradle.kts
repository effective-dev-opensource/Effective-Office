plugins {
    kotlin("jvm")
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}