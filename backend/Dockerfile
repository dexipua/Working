FROM openjdk:21-jdk-slim AS build

RUN apt-get update && apt-get install -y maven curl

COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

# Install curl to check Keycloak availability
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# Add wait-for-keycloak.sh script
COPY wait-for-keycloak.sh /app/wait-for-keycloak.sh
RUN sed -i 's/\r$//' /app/wait-for-keycloak.sh && chmod +x /app/wait-for-keycloak.sh

ENTRYPOINT ["bash", "/app/wait-for-keycloak.sh", "java", "-jar", "/app/backend.jar"]
