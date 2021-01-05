This project is powered by Spring Boot, Docker, and is running on AWS at goreham.no


To start the program:
first run docker-compose up
 (this brings up the database image.)

To migrate the database (currently very hacky)
  change to database project. uncomment flyway maven plugin.
  run mvn flyway:clean-migrate (or just migrate)


Next in the server folder run 
  spring-boot:run -Dspring-boot.run.profiles=dev 

This should run the project (localhost:5000).

To create a jar of the project run.
    (in server folder currently.)
    mvn package spring-boot:repackage
   
   This will create a jar in webServer/target/docker/
    That is runnable (example below).
    java -jar jarFileName.Jar --spring-boot.run.profiles=dev 
    
    
Docker. From the docker file edit it to have the correct release version (from webserver folder)
docker build --tag=tagname:version .
will build and image

To run docker image (locally) 

(use docker network ls to find network name also check docker ps to make sure docker image name is same as in application-dockerdev.properties)
docker run --env ENV=dockerdev --network=webserver_default -it -p 5000:5000  --rm tagname:version

to run docker image normally without local db (here you dont need to mess with the docker network.)
docker run --env ENV=prod -it -p 5000:5000  --rm tagname:version

