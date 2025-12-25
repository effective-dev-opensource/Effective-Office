plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.photo.saver.core"

dependencies {
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    
    // WebFlux for HttpServiceProxyFactoryBuilder
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}