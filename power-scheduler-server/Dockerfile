# 第一阶段：构建
FROM gradle:8.14.0-jdk21-alpine AS builder
LABEL maintainer="771592594@qq.com"
WORKDIR /app
COPY ./ ./
RUN gradle clean :power-scheduler-server:power-scheduler-server-bootstrap:bootJar --no-daemon

## 第二阶段：运行
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/power-scheduler-server/power-scheduler-server-bootstrap/build/libs/app.jar app.jar
RUN apt update && \
    apt install -y vim-tiny iputils-ping curl iproute2 && \
    apt clean
# web端口
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

