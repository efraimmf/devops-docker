# Build
FROM maven:3.9.9-eclipse-temurin-17-alpine as build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Production

FROM eclipse-temurin:17-jre-alpine as production

WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]