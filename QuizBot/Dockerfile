FROM gradle:8.10.1-jdk21-alpine AS BUILD
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM openjdk:21-slim

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/MtquizbotApplication.jar

ENTRYPOINT ["java","-jar","/app/MtquizbotApplication.jar"]