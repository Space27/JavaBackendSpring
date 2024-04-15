FROM eclipse-temurin:21-jre-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8090 8091

ENTRYPOINT ["java", "-jar", "/app.jar"]
