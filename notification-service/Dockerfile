# ---------- Stage 1: Build Dependencies ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy only pom.xml first (for caching dependencies)
COPY pom.xml .

RUN mvn -B dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/notification-service-0.0.1-SNAPSHOT.jar ./notification-service-0.0.1-SNAPSHOT.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "notification-service-0.0.1-SNAPSHOT.jar"]
