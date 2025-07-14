plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
