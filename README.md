# Hospital Management System

A full-stack hospital management application built with Java, Spring Boot, Thymeleaf, MySQL, and Maven.

## Project Overview

This project is designed for managing a hospital's core operations efficiently. It includes modules for patient records, doctor and department management, appointment scheduling, medical records, billing, payment processing, and queue management.

## Features

- Patient management
- Doctor management
- Department management
- Appointment scheduling
- Medical records tracking
- Bill and payment management
- Patient queue system
- Dashboard overview
- Revenue summary dashboard

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven
- GitHub for version control

## Project Structure

- `src/main/java` — backend Java classes and controllers
- `src/main/resources/templates` — frontend Thymeleaf pages
- `src/main/resources/application.properties` — application and database configuration
- `database/schema.sql` — database schema and sample data
- `src/test/java` — test cases for validation

## Prerequisites

- Java 21+
- Maven
- MySQL Server
- MySQL Workbench (optional for database management)

## Database Setup

Create a MySQL database named `hospital_db` and configure the environment variables before running the project:

```bash
export DB_USERNAME=root
export DB_PASSWORD='your_password'
```

Then import the schema file:

```bash
mysql -u root -p < database/schema.sql
```

Important note:
- The project includes the database schema and sample data in `database/schema.sql`.
- A large MySQL Workbench database dump is not uploaded to GitHub because it is usually too large and is not required for project submission.
- The app can be run using the schema file and the configured environment variables.

## Run the Project

```bash
cd "/Users/mayankagrawal/Documents/Hospital Management System/Application/hospital-management"
export DB_USERNAME=root
export DB_PASSWORD='your_password'
./mvnw spring-boot:run
```

The application runs at:

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

This project is a hospital management system developed to streamline patient handling, appointment scheduling, billing, records, payment processing, and queue management for academic submission.
