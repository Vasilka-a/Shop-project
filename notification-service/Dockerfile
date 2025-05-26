FROM openjdk:17-slim
COPY build/libs/*.jar notification.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "notification.jar"]