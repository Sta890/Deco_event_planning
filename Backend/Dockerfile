# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Image de production du backend Spring Boot.
# Utilisée par Render (runtime Docker) : le contexte de build est ce dossier.
# ---------------------------------------------------------------------------

# ---------- Étape 1 : compilation ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Le pom est copié seul en premier : les dépendances restent en cache Docker
# tant qu'il ne change pas.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Étape 2 : exécution ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# On ne tourne pas en root.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Render fournit PORT ; 8070 sert de repli en local.
ENV PORT=8070
EXPOSE 8070

ENTRYPOINT ["java", "-jar", "app.jar"]
