plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation(project(":backend:app"))
}
