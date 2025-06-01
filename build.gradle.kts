plugins {
    kotlin("jvm") version "2.1.20" apply false
}

allprojects {
    group = "tech.powerscheduler"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }

        mavenCentral()
    }
}
