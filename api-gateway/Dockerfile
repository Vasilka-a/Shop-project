FROM openjdk:17-slim
COPY build/libs/*.jar gateway.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "gateway.jar"]