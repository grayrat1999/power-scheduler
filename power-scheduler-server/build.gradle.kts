import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
    jacoco
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

jacoco {
    toolVersion = "0.8.13"
}

subprojects {
    apply(plugin = "jacoco")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
        }
    }

    plugins.withType<KotlinBasePlugin> {
        kotlin {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }

            compilerOptions {
                freeCompilerArgs.add("-Xjsr305=strict")
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        reports {
            junitXml.required.set(false)
            html.required.set(false)
            systemProperty("gradle.build.dir", layout.buildDirectory.get().asFile.absolutePath)
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // 配置执行数据文件/源码代码/类文件的路径
    val executionDataValues = subprojects.map { it.file("build/jacoco/test.exec") }
    val sourceDirectoryValues = subprojects.map { it.sourceSets.main.get().allSource.srcDirs }.flatten()
    val classDirectoryValues = subprojects.map {
        fileTree("${it.layout.buildDirectory.get()}/classes/kotlin/main") {
            exclude(
                "**/dto/**",
                "**/config/**",
                "**/bootstrap/**",
                "**/interfaces/controller/**",
                "**/infrastructure/persistence/model/**",
            )
        }
    }

    executionData(executionDataValues)
    classDirectories.setFrom(classDirectoryValues)
    sourceDirectories.setFrom(sourceDirectoryValues)
}

