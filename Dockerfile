# 첫 번째 스테이지: 빌드
FROM openjdk:17 as builder

RUN apk update && apk add --no-cache findutils # Alpine Linux에서는 apk를 사용
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod -R 755 ./gradlew
RUN ./gradlew clean build -x test -Penv=ci

# 두 번째 스테이지: 실행
FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar

ARG SERVER_MODE
RUN echo "$SERVER_MODE"
ENV SERVER_MODE=$SERVER_MODE

ENTRYPOINT ["java", "-Dspring.profiles.active=${SERVER_MODE}","-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
