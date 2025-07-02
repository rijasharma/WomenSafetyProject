# Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy everything and build the JAR
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]