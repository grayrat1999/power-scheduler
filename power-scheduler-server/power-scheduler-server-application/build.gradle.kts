dependencies {
    implementation(project(":power-scheduler-common"))
    implementation(project(":power-scheduler-server:power-scheduler-server-domain"))
    implementation(libs.bundles.akka)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.29")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    testImplementation(libs.bundles.kotlin.test)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
