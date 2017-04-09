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


Run directly from docker image using docker.io/nileshbhosale/eventstream

>docker run --network="host" nileshbhosale/eventstream




Call Following APIS from Postman (Included postman file in the root folder	

	- http://localhost:8080/apis/v1/stream // To register a new event and persist in the database 
	
	- http://localhost:8080/apis/v1/log-event // To log the registry of the event after saving. Pass on to the listeners
	
	- http://localhost:8080/apis/v1/create-rule // Create new consumer and listen the queue
	
	- http://localhost:8080/apis/v1/dummy-logger // Log Notifications/Alerts
	
	
Design of project:
The application is based on the concept of Topic based Publish and Subscribe. 
Every time a event is created it is sent in a queue to whoever is listening. 
When admin create a rule the application links relative queues to the relavant consumers.
Hence each consumer gets all the events it has subscribed to and can process its logic to handle the business requirement.

- StreamController handles all the events and dumps them in mysql database.
- LogEventController forwards the event to the exchange, which in turn forwards to whoever is listening for particular tag.
- CreateRuleController allows to create rules for business logic. Understands the type of rule and create a suitable Consumer of it. It also binds consumer to listen to current queue with relavant tag.
- Consumer's are created to address occurence of each related event in stream and perform the operation intended.
- DummyController only logs all the alerts coming from Consumer.


	
