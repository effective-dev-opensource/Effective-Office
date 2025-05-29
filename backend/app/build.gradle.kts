plugins {
    id("band.effective.office.backend.spring-boot-application")
}

dependencies {
    implementation(project(":backend:domain"))
    implementation(project(":backend:repository"))
    implementation(project(":backend:feature:authorization"))

    implementation("org.springframework:spring-tx")
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
