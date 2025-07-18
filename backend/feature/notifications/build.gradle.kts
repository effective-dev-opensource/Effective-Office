plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Firebase dependencies
    implementation("com.google.firebase:firebase-admin:9.2.0")

    // Jackson dependencies
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.jsr310)

    implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")

    // Project dependencies
    implementation(project(":backend:feature:booking:core"))
    implementation(project(":backend:feature:calendar-subscription"))
}
