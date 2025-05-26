FROM openjdk:17-slim
COPY build/libs/*.jar cart.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "cart.jar"]