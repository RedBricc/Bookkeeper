# syntax=docker/dockerfile:1.6
# Dependency separation stage
FROM gradle:8-jdk21 AS deps
WORKDIR /app
COPY gradle gradle
COPY gradle.* settings.gradle* build.gradle* ./
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle --no-daemon dependencies

# Build stage
FROM deps AS build
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle clean jooqCodegen bootJar -Pproduction -Pvaadin.productionMode=true -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 9696
RUN useradd -r -u 10001 spring && chown spring:spring /app/app.jar
USER spring:spring
HEALTHCHECK CMD curl -f http://localhost:9696/actuator/health || exit 1
ENTRYPOINT ["java","-jar","app.jar"]
