FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ride-service-0.0.1-SNAPSHOT.jar /app/ride-service.jar

ENTRYPOINT ["java", "-jar", "ride-service.jar"]