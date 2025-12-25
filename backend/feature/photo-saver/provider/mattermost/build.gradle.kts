plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.photo.saver.provider.mattermost"

dependencies {
    implementation(project(":backend:feature:photo-saver:core"))
    implementation(project(":backend:feature:photo-saver:storage:synology"))
    
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    implementation(libs.jakarta)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
