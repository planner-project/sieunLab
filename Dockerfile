FROM openjdk:17 as builder

COPY . /app

WORKDIR /app

RUN ./gradlew build --no-daemon

FROM openjdk:17
COPY --from=builder /app/build/libs/travel-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
