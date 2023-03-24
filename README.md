# CarRentalApp
This repository hosts the backend for the DJS Rental System prototype. It can be used as a standalone API or in conjunction with the DJS-Rental-Car project, which implements a frontend using Vue.js.

## Project Overview
The Car Rental App project implements a RESTful API using the Spring Boot framework. It interacts with an online database to maintain records of vehicle information, user information, and transaction history.

## How to Run
With the entire repository cloned, the "SpringBootApplication.java" file can be run to start the server.
All API endpoints can be used with an endpoint explorer like Postman.
The GET endpoints can be made easily in a web browser with the url pattern "http://localhost:8080/[endpoint]", where [endpoint] options can be found within the RentalController class.

## Components
1. User - Class representing a User of the system. The prototype project does not implement user accounts, and as such, the User just contains contact information.
2. Vehicle - Class representing a Vehicle in the system. A Vehicle object should correspond to a physical vehicle at the rental dealership.
3. AdministrationManager - Class responsible for hosting administrative functions such as CRUD operations on Users and Vehicles. Utilizes the DatabaseManager to refkect administrative actions on the database.
4. DatabaseManager - Class responsible for all database calls and direct interactions. Hosts the PostgreSQL scripts used to interact with the online database.
5. TransactionManager - Class responsible for all transaction-related functions. The prototype does not make "real" transactions, but this is where that logic would go. For now, it contains functions used primarily for transaction logic.
6. RentalService - Class responsible for handling all steps in the rental process. Used in conjunction with the DatabaseManager to handle data to be used by the frontend.
7. RentalController - The RestController of the Spring Boot application. This hosts all of the endpoint implementations of the backend system.
8. SpringBootAppApplication - Default file generated with Spring Framework project. This file is run as a Java application to start up the server.
