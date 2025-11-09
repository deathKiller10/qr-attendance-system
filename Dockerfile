# ----- STAGE 1: Build the Application -----
# Use a standard, official Java 17 image
FROM eclipse-temurin:17-jdk-jammy as builder

# Set a working directory
WORKDIR /app

# Copy the build files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all the dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src ./src

# Build the application .jar file
RUN ./mvnw clean install

# ----- STAGE 2: Create the Final, Smaller Image -----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the .jar file we just built from the "builder" stage
COPY --from=builder /app/target/QRAttendance-0.0.1-SNAPSHOT.jar ./app.jar

# This is the command to run your application.
# Spring Boot will automatically listen on the $PORT variable
# provided by Render, thanks to our application.properties change.
ENTRYPOINT ["java", "-jar", "./app.jar"]