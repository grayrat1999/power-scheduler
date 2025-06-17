dependencies {
    implementation(project(":power-scheduler-common"))
    implementation("com.cronutils:cron-utils:9.2.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
