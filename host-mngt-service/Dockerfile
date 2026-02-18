# ---------- Stage 1: Build ----------
FROM maven:4.0.0-rc-5-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy pom and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jdk-jammy AS deps
WORKDIR /app

# Copy built jar from builder
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]

