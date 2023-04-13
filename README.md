# ABSA technical interview


### Author :
Judge Muzinda
### Email :
jmuzinda@gmail.com

>To access application  from  your favourite browser/REST client use the link below
(http://localhost:8081/swagger-ui/#/).

## Steps to run the application
***

1. Create a database

>Create a  database  of your  favourite which you will use .In this scenario I used mysql e.g database is absa.
>Add the database credentials in application.properties file found in src/main/resources/application.properties

spring.datasource.username=your_sername
spring.datasource.password=your_password
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/absa
spring.jpa.hibernate.ddl-auto=create

This  will create a db for you, check with your favourite workbench

2. Build and run app

>Build your maven  project  using -- mvn clean install
>
>  Change directory to project folder  && java -jar target/AbsaBanking-0.0.1-SNAPSHOT.jar
>
> Access  the endpoints  as  stated above .I have provided some sample  requests  to test in src/test/resources/scratch.txt

4. Run application as a  docker container
>please read src/test/resources/docker-instructions.txt
