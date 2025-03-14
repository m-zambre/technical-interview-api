FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/technical-interview-api.jar app.jar

#Port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
     