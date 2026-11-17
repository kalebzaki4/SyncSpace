# SyncSpace

**REST API for managing clinical operations, from reception to medical schedules.**

**SyncSpace** is a backend application built with Java and Spring Boot to centralize administrative routines in a clinic, including patient registration, management of professionals and rooms, and appointment scheduling.

The application is designed around a clear separation between patients and system users: **patients receive care, while authenticated staff members operate the platform according to their roles and permissions.**

> **Status:** Portfolio project under active development. Current implementation covers JWT authentication, role-based authorization, relational persistence, domain validations, and schedule conflict checks. Interactive API documentation with Swagger/OpenAPI is planned for a future step.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Access Control](#access-control)
- [Endpoints](#endpoints)
- [Setup and Execution](#setup-and-execution)
- [First Usage Flow](#first-usage-flow)
- [Doctors and Specialties](#doctors-and-specialties)
- [Tests](#tests)
- [Next Steps](#next-steps)

## Features

### Authentication and Authorization
- Stateless authentication with JWT.
- Endpoint protection using Bearer tokens.
- Role-based authorization: `ADMIN`, `ATENDENTE`, and `MEDICO`.
- Passwords stored using BCrypt hashing.
- Access control enforced per operation according to the authenticated user's role.

### Clinical Management
- Registration, retrieval, update, and removal of patients.
- Management of clinic rooms and healthcare professionals.
- Creation and maintenance of doctor and receptionist (`ATENDENTE`) profiles.
- Role-based account creation: receptionists create doctor accounts (`MEDICO`) and `ADMIN` creates receptionist accounts (`ATENDENTE`).
- Association of professional profiles with existing user accounts according to domain rules.
- Support for multiple specialties per doctor.
- Appointment scheduling, updating, rescheduling, and cancellation.
- Access to the authenticated doctor's own schedule without exposing other doctors' schedules through that endpoint.

### Consistency and Maintainability
- Request payload validation with Jakarta Bean Validation.
- Request and response DTOs to decouple API contracts from persistence entities.
- Centralized exception handling.
- Duplication checks and validation of entity relationships.
- Schedule conflict checks across patient, doctor, and room.
- Versioned database migrations with Flyway.

## Technologies

| Category | Technologies |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| HTTP API | Spring Web MVC |
| Security | Spring Security, JWT (Auth0 `java-jwt`), and BCrypt |
| Persistence | Spring Data JPA and Hibernate |
| Database | MySQL 8 |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| Tooling | Lombok and Maven |
| Testing | JUnit, Spring Boot Test, and Spring Security Test |

## Architecture

The project follows a layered architecture that separates HTTP communication, business rules, persistence, and security mechanisms.

```text
src/main/java/com/syncspace/api/
├── config/       # Security and infrastructure configuration
├── controller/   # HTTP endpoints and REST contracts
├── dto/          # Request and response objects
├── enums/        # Roles, specialties, and domain values
├── exception/    # Custom exceptions and global error handling
├── model/        # JPA entities
├── repository/   # Database access and queries
├── security/     # JWT issuance, validation, and processing
└── service/      # Business rules and domain validations
