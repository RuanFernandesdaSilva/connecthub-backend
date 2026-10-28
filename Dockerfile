# Etapa 1: Build da aplicacao (com controle de memoria)
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copia o pom.xml e baixa as dependencias (Usa cache se o pom.xml nao mudar)
COPY pom.xml .
RUN MAVEN_OPTS="-Xmx256m" mvn dependency:go-offline -B -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

# 2. Copia o código fonte e compila com limite de 256MB de RAM
COPY src ./src
RUN MAVEN_OPTS="-Xmx256m" mvn clean package -DskipTests -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

# Etapa 2: Execucao do JAR (Leve com Alpine)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT exec java -Xmx384m -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.jar