# 第一阶段：构建
FROM gradle:8.14.0-jdk21-alpine AS builder
LABEL maintainer="771592594@qq.com"
WORKDIR /app

# 先拷贝构建配置文件用于下载依赖
COPY ./gradle ./gradle
COPY ./build.gradle.kts ./settings.gradle.kts ./
COPY ./power-scheduler-common/build.gradle.kts ./power-scheduler-common/
COPY ./power-scheduler-worker/build.gradle.kts ./power-scheduler-worker/
COPY ./power-scheduler-worker-sample/build.gradle.kts ./power-scheduler-worker-sample/
COPY ./power-scheduler-worker-sample/power-scheduler-worker-sample-frameless/build.gradle.kts ./power-scheduler-worker-sample/power-scheduler-worker-sample-frameless/
RUN gradle clean dependencies

# 依赖下载完成后, 再拷贝源代码进行项目构建（目的是利用docker的缓存机制, 每次构建镜像都可以利用之前的构建缓存来节省依赖下载的时间）
COPY ./power-scheduler-common ./power-scheduler-common
COPY ./power-scheduler-worker ./power-scheduler-worker
COPY ./power-scheduler-worker-sample/power-scheduler-worker-sample-frameless ./power-scheduler-worker-sample/power-scheduler-worker-sample-frameless
RUN gradle clean :power-scheduler-worker-sample:power-scheduler-worker-sample-frameless:shadowJar --no-daemon

## 第二阶段：运行
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/power-scheduler-worker-sample/power-scheduler-worker-sample-frameless/build/libs/*.jar app.jar
# 调度服务监听端口
EXPOSE 7758
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
