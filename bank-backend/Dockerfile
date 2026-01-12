# ===== Build stage =====
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom primero para aprovechar cache
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Descarga dependencias
RUN mvn -q -DskipTests dependency:go-offline

# Copiamos código y construimos
COPY src/ src/
RUN mvn -q -DskipTests package

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
