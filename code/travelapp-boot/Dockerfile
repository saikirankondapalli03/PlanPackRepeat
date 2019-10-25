FROM openjdk:8-jdk-alpine
# Add Maintainer Info
LABEL maintainer="saikirankondapalli03@gmail.com"
ADD ["target/travelapp-0.0.1-SNAPSHOT.jar", "travelapp-0.0.1-SNAPSHOT.jar"]
EXPOSE 5000
EXPOSE 8787
RUN sh -c 'touch /travelapp-0.0.1-SNAPSHOT.jar'
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8787,suspend=n  -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev -jar /travelapp-0.0.1-SNAPSHOT.jar" ]
