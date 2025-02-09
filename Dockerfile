# Use the official Maven image to build the project
FROM maven:3.9.9-amazoncorretto-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code to the container
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# creaate image with Amazon Corretto JDK 21
FROM amazoncorretto:21.0.4

# Set the working folder and copy the packaged application from the build stage
WORKDIR /app
COPY --from=build /app/target/user-registration-0.0.1-SNAPSHOT.jar /app/user-registration-0.0.1-SNAPSHOT.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "user-registration-0.0.1-SNAPSHOT.jar"]