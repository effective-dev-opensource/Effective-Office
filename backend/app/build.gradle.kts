plugins {
    id("band.effective.office.backend.spring-boot-application")
}

dependencies {
    implementation(project(":backend:core:domain"))
    implementation(project(":backend:core:repository"))
    implementation(project(":backend:feature:user"))
    implementation(project(":backend:feature:booking:core"))
    implementation(project(":backend:feature:booking:calendar:google"))
    implementation(project(":backend:feature:booking:calendar:dummy"))
    implementation(project(":backend:feature:workspace"))
    implementation(project(":backend:feature:authorization"))

    implementation("org.springframework:spring-tx")
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // .env file support
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
}
