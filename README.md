# To-Do list implemented with RESTful API

## Context
This project implements a simple RESTful API for managing tasks inside companies using a to-do list.

## Users
- **Standard Users**: Can manage only their own tasks.
- **Company-Admin Users**: Can manage their own tasks and the tasks of all users in their company.
- **Super Users**: Have access to all tasks across all companies.

## Project
The project uses Spring Boot and Tomcat as an embedded server.

There is no database used with the project, rather all the necessary data is stored in-memory, using the ConcurrentHashMap, to provide a thread-safe structure, so that the concurrent requests can be handled successfully.

Maven is used for building the project and dependency management.

After starting the project, check apidoc.html for API documentation. Project runs on port 8080 (defined in application.properties).

## Prerequisites
- Java 17 (or higher)
- Maven Wrapper (included in the project)

## Test, Clean + Build, Run

#### Run the tests

mvnw test

#### Clean and Build the project

mvnw clean package

#### Run

mvnw spring-boot:run

or

java -jar target/saas-todo-api-0.0.1-SNAPSHOT.jar



#### This project is licensed under the MIT License. See the LICENSE file for details.