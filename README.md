Follow the steps below to run the application:

1. Install Maven on computer. 
http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

2. Install Mysql Server 5.6
http://dev.mysql.com/doc/refman/5.6/en/installing.html
Username: root
Password: root
Make sure the mysql server is running on localhost:3306.

3. Install RabbitMQ
https://www.rabbitmq.com/download.html

4. Create a database "eventstream" in mysql.

5. Install docker
https://store.docker.com/search?type=edition&offering=community

6. Browse to the folder location checked out from master branch from terminal.
run command 
>mvn clean
>mvn install
>docker build -f DockerFile -t eventstream .
>docker run --network="host" eventstream
(
Run directly from docker image using docker.io/nileshbhosale/eventstream
>docker run --network="host" nileshbhosale/eventstream
)


Call Following APIS from Postman (Included postman file in the root folder	)
	- http://localhost:8080/apis/v1/stream // To register a new event and persist in the database  
	- http://localhost:8080/apis/v1/log-event // To log the registry of the event after saving. Pass on to the listeners
	- http://localhost:8080/apis/v1/create-rule // Create new consumer and listen the queue
	- http://localhost:8080/apis/v1/dummy-logger // Log Notifications/Alerts
	