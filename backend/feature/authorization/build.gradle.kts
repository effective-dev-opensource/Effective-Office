plugins {
    id("band.effective.office.backend.spring-boot-common")
}

dependencies {
    implementation(project(":backend:domain"))
    implementation(project(":backend:repository"))

    // Spring Security
    implementation(libs.spring.boot.starter.security)

    // JWT
    implementation(libs.bundles.jwt)

    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
