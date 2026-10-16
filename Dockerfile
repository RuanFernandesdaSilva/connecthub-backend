# Etapa 1: Build da aplicacao
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Execucao do JAR
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT exec java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.jar