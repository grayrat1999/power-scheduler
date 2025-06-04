FROM eclipse-temurin:21-jre

WORKDIR /app

ENV TZ=Asia/Shanghai
ENV JVM_OPTIONS=""
ENV ARGS=""

RUN apt update && \
    apt install -y vim-tiny iputils-ping curl tini && \
    apt clean

COPY ./power-scheduler-server-bootstrap/build/libs/app.jar app.jar

EXPOSE 8080

LABEL maintainer="grayrat1999@163.com" \
    org.opencontainers.image.source="https://github.com/grayrat1999/power-scheduler" \
    org.opencontainers.image.description="PowerScheduler server application" \
    org.opencontainers.image.licenses="MIT"

ENTRYPOINT ["/usr/bin/tini", "--", "sh", "-c", "exec java $JVM_OPTIONS -jar /app/app.jar $ARGS"]
