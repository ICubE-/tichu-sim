# Stage 1: Build the JAR
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Final Image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/tichu-sim-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
