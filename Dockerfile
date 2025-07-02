FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
# 缓存依赖
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]