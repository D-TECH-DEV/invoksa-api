FROM eclipse-temurin:17-jdk-focal

LABEL authors="mentor"

WORKDIR /app

COPY target/invoksa_springboot-0.0.1-SNAPSHOT.jar /app/invoksa.jar

ENTRYPOINT ["java", "-jar", "invoksa.jar"]