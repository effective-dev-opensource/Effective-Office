FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy gradle files
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle.properties .
COPY buildSrc buildSrc
COPY backend/app app
COPY backend/domain domain
COPY backend/repository repository

# Build the application
RUN ./gradlew clean build -x test

# Extract the layers
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG JAR_FILE=/workspace/app/app/build/libs/app-boot.jar
COPY --from=build ${JAR_FILE} app.jar

# Set up environment variables
ENV SPRING_PROFILES_ACTIVE=live
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
