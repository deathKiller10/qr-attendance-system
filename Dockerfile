# Use a standard, official Java 17 (or 21) image from the Eclipse Foundation
FROM eclipse-temurin:17-jdk-jammy

# Set a working directory inside the container
WORKDIR /app

# Copy the Maven wrapper (if you have it)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all the dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src ./src

# Build the application .jar file
RUN ./mvnw clean install

# --- This is the second stage ---
# Now, create a smaller, more secure final image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the .jar file we just built from the first stage
COPY --from=0 /app/target/QRAttendance-0.0.1-SNAPSHOT.jar ./app.jar

# Tell Java to run on our port 8085
ENV PORT 8085
EXPOSE 8085

# This is the command to run your application
ENTRYPOINT ["java", "-jar", "./app.jar"]