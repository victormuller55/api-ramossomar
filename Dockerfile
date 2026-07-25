FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY target/ramossomar-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7000
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
