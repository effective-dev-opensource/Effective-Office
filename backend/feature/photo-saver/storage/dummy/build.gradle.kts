plugins {
    id("band.effective.office.backend.spring-data-jpa")
}

group = "band.effective.office.backend.photo.saver.storage.dummy"

dependencies {
    implementation(project(":backend:feature:photo-saver:core"))
}
