FROM openjdk:17-slim
WORKDIR /app
COPY build/libs/*.jar notification.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "notification.jar"]