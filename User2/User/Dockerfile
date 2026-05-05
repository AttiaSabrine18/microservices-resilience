# Build avec Maven + Runtime dans une seule étape
FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

# Copier les sources
COPY pom.xml .
COPY src ./src

# Builder le JAR
RUN mvn clean package -DskipTests

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "target/*.jar"]
