# =========================
# 1️⃣ BUILD STAGE
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiamos solo lo necesario para cache
COPY pom.xml .
COPY agora-notifications-core/pom.xml agora-notifications-core/pom.xml
COPY agora-notifications-api/pom.xml agora-notifications-api/pom.xml

RUN mvn -B -q dependency:go-offline

# Copiamos el resto del proyecto
COPY . .

# Compilar todo (core + api)
RUN mvn clean package -DskipTests

# =========================
# 2️⃣ RUNTIME STAGE
# =========================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /build/agora-notifications-api/target/*.jar app.jar
COPY docker/entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["./entrypoint.sh"]
