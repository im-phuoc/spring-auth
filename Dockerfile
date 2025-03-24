# Build stage
FROM maven:3.9-amazoncorretto-17 AS builder
WORKDIR /app

# Copy maven files first
COPY backend/.mvn/ ./.mvn/
COPY backend/mvnw backend/mvnw.cmd ./
RUN chmod +x mvnw

# Copy pom.xml and download dependencies
COPY backend/pom.xml .
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 