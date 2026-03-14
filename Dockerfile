FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
WORKDIR /build/devices-service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /build/devices-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]