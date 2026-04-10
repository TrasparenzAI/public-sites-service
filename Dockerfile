FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package -Dmaven.repo.local=/root/.m2/repository

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/target/public-sites-service-0.5.0.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
