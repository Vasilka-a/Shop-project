FROM openjdk:17-slim
WORKDIR /app
COPY build/libs/*.jar gateway.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "gateway.jar"]