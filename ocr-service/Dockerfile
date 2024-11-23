FROM openjdk:17-jdk-slim

RUN apt-get update && \
    apt-get install -y tesseract-ocr && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY tessdata /app/tessdata

COPY target/ocr-service-0.0.1-SNAPSHOT.jar app.jar

ENV TESSDATA_PREFIX=/app/tessdata/

ENTRYPOINT ["java", "-jar", "app.jar"]
