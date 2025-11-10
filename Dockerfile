FROM openjdk:8-jre-alpine

# ❌ Image ancienne avec vulnérabilités
COPY target/*.jar app.jar

# ❌ Utilisateur root
USER root

ENTRYPOINT ["java", "-jar", "/app.jar"]
