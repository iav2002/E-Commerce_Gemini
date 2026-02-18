# Stage 1: Build the Spring Boot app
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Stage 2: Run with lightweight JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
