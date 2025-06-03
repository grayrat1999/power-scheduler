# 第一阶段：构建
FROM gradle:8.14.0-jdk21-alpine AS builder
LABEL maintainer="771592594@qq.com"
WORKDIR /app

# 先拷贝构建配置文件用于下载依赖
COPY ./gradle ./gradle
COPY ./build.gradle.kts ./settings.gradle.kts ./
COPY ./power-scheduler-common/build.gradle.kts ./power-scheduler-common/
COPY ./power-scheduler-server/build.gradle.kts ./power-scheduler-server/
COPY ./power-scheduler-server/power-scheduler-server-application/build.gradle.kts ./power-scheduler-server/power-scheduler-server-application/
COPY ./power-scheduler-server/power-scheduler-server-bootstrap/build.gradle.kts ./power-scheduler-server/power-scheduler-server-bootstrap/
COPY ./power-scheduler-server/power-scheduler-server-domain/build.gradle.kts ./power-scheduler-server/power-scheduler-server-domain/
COPY ./power-scheduler-server/power-scheduler-server-infrastructure/build.gradle.kts ./power-scheduler-server/power-scheduler-server-infrastructure/
COPY ./power-scheduler-server/power-scheduler-server-interface/build.gradle.kts ./power-scheduler-server/power-scheduler-server-interface/
RUN gradle clean dependencies

# 依赖下载完成后, 再拷贝源代码进行项目构建（目的是利用docker的缓存机制, 每次构建镜像都可以利用之前的构建缓存来节省依赖下载的时间）
COPY ./power-scheduler-common ./power-scheduler-common
COPY ./power-scheduler-server ./power-scheduler-server
RUN gradle :power-scheduler-server:power-scheduler-server-bootstrap:bootJar --no-daemon
#ENTRYPOINT ["tail", "-f", "/dev/null"]

# 第二阶段：运行
FROM eclipse-temurin:21-jre

ENV TZ=Asia/Shanghai
ENV JVM_OPTIONS=""
ENV ARGS=""

WORKDIR /app

RUN apt update && \
    apt install -y vim-tiny iputils-ping curl tini && \
    apt clean

COPY --from=builder /app/power-scheduler-server/power-scheduler-server-bootstrap/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["/usr/bin/tini", "--", "sh", "-c", "exec java $JVM_OPTIONS -jar /app/app.jar $ARGS"]

