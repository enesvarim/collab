FROM maven:3.9.5-eclipse-temurin-21-jdk AS build

WORKDIR /app

COPY ./pom.xml ./app
COPY ./src ./app/src

RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]