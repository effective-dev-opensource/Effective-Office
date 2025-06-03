plugins {
    id("band.effective.office.backend.spring-boot-common")
}

dependencies {
    implementation(project(":backend:core:domain"))
    implementation(project(":backend:feature:user"))

    // Spring Security
    implementation(libs.spring.boot.starter.security)

    // JWT
    implementation(libs.bundles.jwt)

    // Google API Client for ID token validation
    implementation(libs.bundles.google.api)

    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
