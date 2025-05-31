# 第一阶段：构建
FROM gradle:8.14.0-jdk21-alpine AS builder
LABEL maintainer="771592594@qq.com"
WORKDIR /app
COPY ./ ./
RUN gradle clean :power-scheduler-worker-sample:power-scheduler-worker-sample-springboot:bootJar --no-daemon

## 第二阶段：运行
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/power-scheduler-worker-sample/power-scheduler-worker-sample-springboot/build/libs/*.jar app.jar
# 调度服务监听端口
EXPOSE 25258
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
