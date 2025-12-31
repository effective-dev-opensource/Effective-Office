plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.photo.saver.storage.synology"

dependencies {
    implementation(project(":backend:feature:photo-saver:core"))
    
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
