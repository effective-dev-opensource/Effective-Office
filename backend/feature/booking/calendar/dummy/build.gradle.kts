plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    implementation(project(":backend:feature:booking:core"))

    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}