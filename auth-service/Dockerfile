FROM openjdk:17-slim
COPY build/libs/*.jar auth.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "auth.jar"]