# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk

RUN apt-get update && \
    apt-get install -y tesseract-ocr && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY tessdata /app/tessdata
COPY --from=build /app/target/*.jar /app/ocr-service.jar

ENV TESSDATA_PREFIX=/app/tessdata/

ENTRYPOINT ["java", "-Djava.net.preferIPv6Addresses=true", "-jar", "/app/ocr-service.jar"]