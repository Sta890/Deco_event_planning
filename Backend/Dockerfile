# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Image de production du backend Spring Boot.
# Utilisée par Render (runtime Docker) : le contexte de build est ce dossier.
#
# L'offre gratuite Render alloue 0,1 CPU et 512 Mo de RAM. La JVM limiterait par
# défaut le tas à 25 % de 512 Mo (~128 Mo), insuffisant pour Spring Boot : voir
# JAVA_OPTS à l'étape 2.
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

# Le conteneur ne tourne pas en root.
RUN addgroup -S spring && adduser -S -G spring spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Render fournit PORT ; 8070 sert de repli en local.
ENV PORT=8070

# Sur une instance à 512 Mo, les 25 % par défaut ne laissent que ~128 Mo de tas.
# 70 % laisse ~150 Mo au metaspace, aux piles de threads et au code cache.
# ExitOnOutOfMemoryError fait redémarrer le conteneur plutôt que de le figer.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8070

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
