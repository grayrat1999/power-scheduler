import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    id("java-library")
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
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(project(":power-scheduler-worker"))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
}

tasks.test {
    useJUnitPlatform()
}