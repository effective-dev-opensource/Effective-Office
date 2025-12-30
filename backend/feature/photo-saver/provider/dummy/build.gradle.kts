plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.photo.saver.provider.dummy"

dependencies {
    implementation(project(":backend:feature:photo-saver:core"))
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
