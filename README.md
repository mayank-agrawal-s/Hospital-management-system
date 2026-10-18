# Hospital Management System

A full-stack hospital management application built with Java, Spring Boot, Thymeleaf, MySQL, and Maven.

## Features

- Patient management
- Doctor management
- Department management
- Appointment scheduling
- Medical records tracking
- Bill and payment management
- Patient queue system
- Dashboard overview

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

## Project Structure

- `src/main/java` — backend Java classes and controllers
- `src/main/resources/templates` — Thymeleaf frontend pages
- `src/main/resources/application.properties` — database configuration

## Prerequisites

- Java 21+
- Maven
- MySQL server

## Database Setup

Create a MySQL database named `hospital_db` and set the environment variables before starting the project:

```bash
export DB_USERNAME=root
export DB_PASSWORD='your_password'
```

## Run the Project

```bash
cd "/Users/mayankagrawal/Documents/Hospital Management System/Application/hospital-management"
export DB_USERNAME=root
export DB_PASSWORD='your_password'
./mvnw spring-boot:run
```

The application will run at:

```text
http://localhost:8080
```

## Access from Mobile / Other Devices

On the same Wi-Fi network, use:

```text
http://10.176.30.244:8080
```

## GitHub Repository

```text
https://github.com/mayank-agrawal-s/Hospital-management-system.git
```

## College Submission Note

This project is a hospital management system developed to manage departments, doctors, patients, appointments, records, bills, payments, and queue services.
