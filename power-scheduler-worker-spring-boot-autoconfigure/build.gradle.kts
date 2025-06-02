import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("java-library")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain {
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
    api(project(":power-scheduler-worker"))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
}

tasks.test {
    useJUnitPlatform()
}