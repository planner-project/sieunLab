FROM openjdk:17 as builder

RUN apt-get update && apt-get install -y findutils

COPY . /app

WORKDIR /app

RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

FROM openjdk:17
COPY --from=builder /app/build/libs/travel-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
