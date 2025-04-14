FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Add Swagger profile to make sure Swagger is enabled in production
RUN mvn clean package -Dmaven.test.skip=true -Dspring.profiles.active=prod

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Use environment variables to enable Swagger in production
CMD ["java", "-jar", "app.jar", "--springdoc.swagger-ui.enabled=true", "--springdoc.api-docs.enabled=true"]