# ===== STAGE 1: Build the app =====
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

# Copy all source files
COPY src ./src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# ===== STAGE 2: Run the app =====
FROM eclipse-temurin:21-jdk-jammy

# Install required system libraries
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    libfreetype6-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Enable headless mode (prevents graphics issues)
ENV JAVA_OPTS="-Djava.awt.headless=true"

# Run the Spring Boot app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]