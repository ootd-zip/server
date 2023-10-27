FROM openjdk:17 as builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN chmod -R 755 /.gradle
RUN ./gradlew clean build -x test -Penv=ci

FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar

ARG SERVER_MODE
RUN echo "$SERVER_MODE"
ENV SERVER_MODE=$SERVER_MODE

ENTRYPOINT ["java", "-Dspring.profiles.active=${SERVER_MODE}","-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]