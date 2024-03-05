# Use an official Maven image as the base image
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
# Build the application using Maven
RUN mvn clean package -DskipTests
# Use an official OpenJDK image as the base image
FROM eclipse-temurin:21.0.1_12-jre-jammy
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/kubernetes-client-sandbox-0.0.1-SNAPSHOT.jar .
# Set the command to run the application
CMD ["java", "-jar", "kubernetes-client-sandbox-0.0.1-SNAPSHOT.jar"]