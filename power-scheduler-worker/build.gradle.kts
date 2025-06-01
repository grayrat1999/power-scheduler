import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":power-scheduler-common"))
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("commons-io:commons-io:2.19.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")

    runtimeOnly("com.h2database:h2:1.4.200")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.7.18")
}

tasks.test {
    useJUnitPlatform()
}