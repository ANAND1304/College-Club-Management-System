# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/college-club-management-1.0.0.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run with prod profile
ENTRYPOINT ["java", "-jar", "-Dserver.port=8080", "app.jar", "--spring.profiles.active=prod"]
