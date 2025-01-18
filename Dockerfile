FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/outdry-backend-0.0.1-SNAPSHOT.jar /app/outdry-backend-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/outdry-backend-0.0.1-SNAPSHOT.jar"]