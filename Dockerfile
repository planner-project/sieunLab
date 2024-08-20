FROM openjdk:17-alpine as builder

RUN apk update && apk add findutils

COPY . /app

WORKDIR /app

RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

FROM openjdk:17-alpine
COPY --from=builder /app/build/libs/travel-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
