# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/backend.jar
EXPOSE 8090
CMD ["java", "-Djava.net.preferIPv6Addresses=true", "-jar", "/app/backend.jar"]