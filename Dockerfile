# Development
FROM eclipse-temurin:17 AS dev
WORKDIR app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew

RUN ./gradlew clean --no-daemon

ENV SPRING_PROFILES_ACTIVE=dev
ENV TZ=Asia/Seoul

EXPOSE 8081

ENTRYPOINT ["./gradlew", "bootRun", "--no-daemon"]

# Production
## Stage 1. Build
FROM eclipse-temurin:17 AS builder

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
COPY src src
RUN chmod +x gradlew

RUN ./gradlew build -x test

## Stage 2. Production
FROM eclipse-temurin:17-alpine AS prod
WORKDIR /app

COPY --from=builder build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=Asia/Seoul

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
