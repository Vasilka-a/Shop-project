FROM openjdk:17-slim
COPY build/libs/*.jar front.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "front.jar"]