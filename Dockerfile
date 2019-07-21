FROM adoptopenjdk:11-jre-hotspot
EXPOSE 5000
ADD /target/docker/server-1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${ENV}",  "-jar", "app.jar"]