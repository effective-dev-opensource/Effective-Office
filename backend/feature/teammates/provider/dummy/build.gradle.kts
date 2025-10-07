plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.teammates.provider.dummy"

dependencies {
    implementation(project(":backend:feature:teammates:core"))
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}