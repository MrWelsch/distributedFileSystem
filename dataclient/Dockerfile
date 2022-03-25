FROM openjdk:11-jdk as build

USER root

WORKDIR /build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN chmod -R 777 ./mvnw

RUN ./mvnw clean package -DskipTests

###

FROM openjdk:11-jre-slim as run

COPY --from=build /build/target/*.jar /v-filesystem-dataclient.jar

ENTRYPOINT ["java", "-jar", "/v-filesystem-dataclient.jar"]