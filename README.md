Follow the steps below to run the application:

1. Install Maven on computer. 
http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

2. Install Mysql Server 5.6
http://dev.mysql.com/doc/refman/5.6/en/installing.html
Username: root
Password: root

3. Create a database "eventstream"

4. Browse to the folder location from cmd.
run command 
>mvn clean
>mvn install
>mvn spring-boot:run

5. Call Following APIS 
	- http://localhost:8080/apis/v1/stream // To register a new event and persist in the database  
	- http://localhost:8080/apis/v1/log-event // To log the registry of the event after saving. Pass on to the listeners
	- http://localhost:8080/apis/v1/create-rule // Create new consumer and listen the queue
	- http://localhost:8080/apis/v1/dummy-logger // Log Notifications/Alerts
	