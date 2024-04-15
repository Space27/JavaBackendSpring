FROM eclipse-temurin:21-jre-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV TELEGRAM_API_KEY=${TELEGRAM_API_KEY}

EXPOSE 8090 8091

ENTRYPOINT ["java", "-jar", "/app.jar"]
