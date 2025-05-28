FROM openjdk:17-slim
WORKDIR /app
COPY build/libs/*.jar front.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "front.jar"]