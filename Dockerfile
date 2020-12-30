FROM java:8-jdk-alpine

COPY ./build/McNativePlayerService.jar /usr/app/

WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "McNativePlayerService.jar"]