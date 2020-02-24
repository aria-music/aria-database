FROM openjdk:11
COPY . /usr/src/aria-database
WORKDIR /usr/src/aria-database

RUN ./gradlew --no-daemon -i bootJar

VOLUME /usr/src/aria-database/config
CMD [ "java", "-jar", "./build/libs/aria.jar" ]
