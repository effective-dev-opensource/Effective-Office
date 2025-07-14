FROM eclipse-temurin:17-jre

# Устанавливаем curl (если нужен для healthcheck'ов)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копируем JAR-файл из текущего контекста
COPY *.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]