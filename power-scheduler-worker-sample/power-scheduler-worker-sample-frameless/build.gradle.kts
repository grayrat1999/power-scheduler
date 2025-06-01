plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(8)
}

dependencies {
    implementation(project(":power-scheduler-worker"))
    implementation("ch.qos.logback:logback-classic:1.5.18")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

application {
    mainClass.set("tech.powerscheduler.worker.sample.frameless.FramelessApp")
}

tasks {
    shadowJar {
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
        manifest {
            attributes(
                mapOf("Main-Class" to "tech.powerscheduler.worker.sample.frameless.FramelessApp")
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }
}