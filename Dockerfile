# ====== Build Stage ======
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Crear el Maven wrapper para uso en Docker
RUN apk add --no-cache maven

# Compilar y empaquetar (sin tests durante build)
RUN mvn clean package -DskipTests

# ====== Runtime Stage ======
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar solo el JAR construido
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
