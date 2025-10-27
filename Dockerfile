# Use a slim Java runtime as base
FROM eclipse-temurin:21-jdk-jammy

# Install required native libs for font rendering
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    libfreetype6-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven/Gradle project files and build output
COPY target/*.jar app.jar

# Run in headless mode
ENV JAVA_OPTS="-Djava.awt.headless=true"

# Start app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
