FROM openjdk:8
MAINTAINER Pyshnyi Fedir <fed.lviv@gmail.com>
ADD build/libs/RESTfulTemplate-0.0.1-SNAPSHOT.jar rest.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rest.jar"]