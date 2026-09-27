ARG MODULE

FROM eclipse-temurin:21-jdk AS build
ARG MODULE
WORKDIR /src
COPY . .
RUN ./gradlew :${MODULE}:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /opt/workspace
ARG MODULE
COPY --from=build /src/${MODULE}/build/libs/*.jar application.jar
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "application.jar"]
