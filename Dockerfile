# syntax=docker/dockerfile:1.6
ARG JAVA_VERSION=24

# Build requires PostgreSQL 17 for JOOQ code generation
FROM postgres:17 AS build

ARG JAVA_VERSION

ENV DB_NAME=bookkeeper_gen \
    DB_USER=bookkeeper \
    DB_PASSWORD=admin \
    JAVA_VERSION=$JAVA_VERSION \
    GRADLE_VERSION=8.14 \
    JAVA_HOME=/opt/java \
    PATH="${PATH}:${JAVA_HOME}/bin"

RUN apt update && \
    # Install dependencies
    apt install -y --no-install-recommends curl unzip gnupg ca-certificates && \
    # Install JDK 24
    curl -fsSL https://api.adoptium.net/v3/binary/latest/${JAVA_VERSION}/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk \
        -o /tmp/jdk.tar.gz && \
    mkdir -p ${JAVA_HOME} && tar --strip-components=1 -xzf /tmp/jdk.tar.gz -C ${JAVA_HOME} && \
    # Install Gradle
    curl -fsSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    unzip -q gradle.zip -d /opt && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle && \
    rm -rf /var/lib/apt/lists/* /tmp/* gradle.zip

WORKDIR /workspace
COPY . .

RUN set -eux; \
    # Set up gen database
    chown -R postgres:postgres /workspace /var/lib/postgresql; \
    su postgres -c "initdb"; \
    su postgres -c "pg_ctl -o \"-c listen_addresses='localhost'\" -w start"; \
    su postgres -c "psql -f /workspace/db-init/init_bookkeeper_gen.sql"; \
    # Build the application
    ./gradlew clean jooqCodegen bootJar -x test --no-daemon \
    -Dgen.db.url=jdbc:postgresql://localhost:5432/${DB_NAME}; \
    su postgres -c "pg_ctl -m fast stop"; \
    rm -f /workspace/db-init/init_bookkeeper_gen.sql

FROM eclipse-temurin:${JAVA_VERSION}-jre AS runtime

COPY --from=build /workspace/build/libs/*-SNAPSHOT.jar /app/app.jar

WORKDIR /app
RUN useradd -r -u 10001 spring && chown spring:spring /app/app.jar
USER spring:spring

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 9696
HEALTHCHECK --interval=30s --retries=3 \
  CMD wget -q --spider http://127.0.0.1:9696/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
