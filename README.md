# user-registration
Functionality requirements:
1. The system shall provide a home page to the user
2. On the home page, user can login with existing credentials, or register a new account
3. User account information shall include: first name, last name, email, password, and birthday
4. After logged in, user shall be presented with his/her account information, and user can edit the account information (including password)
5. Logged in user shall be able to logout

## Tech stack
* Build tool: maven 3.9.9
* Java: 21
* Framework:
  * Spring boot 3.4.2
  * Spring mvc
  * Spring security
* DBMS: MySQL 8.0.36
* Thymeleaf: Template engine
* Bootstrap: CSS framework

## Start application
`mvn spring-boot:run`

## Build application
`mvn clean package`

## Docker guideline
### Pull docker image
* `docker pull nhloi/user-registration:0.0.1`
### Create network:
* `docker network create user-registration-network`
### Start MySQL in user-registration-network
* `docker run --network user-registration-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian`
* create database `user_registration_db` in MySQL 
### Run application in user-registration-network
* `docker run --name user-registration --network user-registration-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/user_registration_db nhloi/user-registration:0.0.1`

After application started, the web application is available on http://localhost:8080/
![img.png](img.png)
