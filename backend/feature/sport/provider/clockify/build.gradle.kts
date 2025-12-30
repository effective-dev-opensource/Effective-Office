plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.sport.provider.clockify"

dependencies {
    implementation(project(":backend:feature:sport:core"))
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.webflux)
}