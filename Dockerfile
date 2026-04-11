FROM eclipse-temurin:21-jdk

LABEL authors="mentor"

WORKDIR /app

COPY target/invoksa_springboot-0.0.1-SNAPSHOT.jar /app/invoksa.jar

ENTRYPOINT ["java", "-jar", "invoksa.jar"]