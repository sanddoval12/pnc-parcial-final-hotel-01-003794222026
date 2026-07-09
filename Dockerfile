# ---- Etapa 1: build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

COPY src src

# -x test: las pruebas ya corren aparte en el pipeline de CI (Parte X).
# Aquí solo empaquetamos el jar.
RUN ./gradlew clean bootJar --no-daemon -x test

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
