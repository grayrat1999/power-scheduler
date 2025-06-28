plugins {
    kotlin("jvm") version "2.1.20" apply false
    id("com.vanniktech.maven.publish") version "0.32.0" apply false
}

allprojects {
    group = "tech.powerscheduler"
    version = "4.0.0"

    repositories {
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }

        mavenCentral()
    }
}
