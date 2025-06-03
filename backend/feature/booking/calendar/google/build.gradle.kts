plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    implementation(project(":backend:core:domain"))
    implementation(project(":backend:feature:booking:core"))

    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Google Calendar API
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20230707-2.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.3.0")
}
