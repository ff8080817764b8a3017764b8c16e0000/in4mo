####Build status:

[![Codeship Status for arturk9/in4mo](https://app.codeship.com/projects/ae14e429-0134-4f33-bfc5-71d9045b4761/status?branch=master)](https://app.codeship.com/projects/425749)

####Tested on:
- MacOS 11.1
- JDK Corretto Build 11.0.8.10.1
- Apache Maven 3.6.3

####Compile:
````
mvn clean install
````

####Run:
````
cd target
java -jar wallet-1.0-SNAPSHOT.jar
````

####API Docs:
````
http://localhost:8080/swagger-ui.html
````

![Screenshot](api_docs.png)