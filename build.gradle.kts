allprojects {
    group = "org.grayrat"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }

        mavenCentral()
    }
}
