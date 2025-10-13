# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app


# This allows Docker to cache this layer if only your source code changes.
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make the wrapper script executable
RUN chmod +x ./mvnw

# Now copy the source code
COPY src ./src

# Run the build using the wrapper
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]