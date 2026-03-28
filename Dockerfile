# =============================================================================
# Etapa 1 — Construcción del JAR con Maven
# =============================================================================
FROM maven:3.9-eclipse-temurin-21 AS construccion

WORKDIR /app

# Descarga dependencias primero (aprovecha caché de capas)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compila el proyecto
COPY src ./src
RUN mvn package -DskipTests -B

# =============================================================================
# Etapa 2 — Imagen de ejecución (solo JRE, imagen mínima)
# =============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia únicamente el JAR generado
COPY --from=construccion /app/target/*.jar app.jar

# Directorio para fotos subidas (montado como volumen en producción)
RUN mkdir -p uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
