# syntax=docker/dockerfile:1.6
FROM eclipse-temurin:24-jre

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar

WORKDIR /app
RUN useradd -r -u 10001 spring \
 && chown spring:spring /app/app.jar
USER spring:spring

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 9696
HEALTHCHECK --interval=15s --retries=3 \
  CMD wget -q --spider http://localhost:9696/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
