FROM openjdk:17-slim
WORKDIR /app
COPY build/libs/*.jar product.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "product.jar"]