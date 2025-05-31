plugins {
    id("java")
}

dependencies {
    implementation(project(":power-scheduler-worker"))
    implementation("ch.qos.logback:logback-classic:1.5.18")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}
