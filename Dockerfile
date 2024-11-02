FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle.kts gradle.properties settings.gradle.kts ./
RUN gradle shadowJar -x test --no-daemon
COPY src ./src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar curexcratebot.jar

ENTRYPOINT ["java", "-jar", "curexcratebot.jar"]
