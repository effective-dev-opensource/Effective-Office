plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

dependencies {
    // Spring Security
    implementation(libs.spring.boot.starter.security)

    // Spring Data JPA
    implementation(libs.spring.boot.starter.data.jpa)

    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
}
