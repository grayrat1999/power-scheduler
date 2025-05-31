import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":power-scheduler-common"))
    implementation(project(":power-scheduler-server:power-scheduler-server-interface"))
    implementation(project(":power-scheduler-server:power-scheduler-server-application"))
    implementation(project(":power-scheduler-server:power-scheduler-server-infrastructure"))
    implementation(project(":power-scheduler-server:power-scheduler-server-domain"))

    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.getByName<BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
