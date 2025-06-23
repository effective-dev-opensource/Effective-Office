plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    // Google Calendar API
    implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.3.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
}
