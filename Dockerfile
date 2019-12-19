FROM openjdk:8
MAINTAINER Pyshnyi Fedir <fed.lviv@gmail.com>
ENV SPRING_PROFILES_ACTIVE local
RUN groupadd -r user_grp && useradd -r -g user_grp user
ADD build/libs/RESTfulTemplate-0.0.1-SNAPSHOT.jar rest.jar
ENTRYPOINT ["java", "-jar", "rest.jar"]