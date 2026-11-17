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

```

### Layer Responsibilities

* **Controllers:** Receive HTTP requests and return API responses.
* **Services:** Centralize business rules, including validations, links between professionals and user accounts, and schedule availability.
* **Repositories:** Abstract data access through Spring Data JPA.
* **Models:** Represent entities persisted in the database.
* **DTOs:** Define the data accepted and returned by the API, avoiding direct exposure of persistence entities.
* **Security:** Implements the authentication flow and JWT token validation.

This separation supports code maintainability, safe evolution of business rules, and component testability.

## Access Control

Internal users are differentiated based on their responsibilities.

| Role | Responsibilities |
| --- | --- |
| `ADMIN` | Administers the system and can create user accounts for receptionists (`ATENDENTE`). Can remove doctor and receptionist profiles. |
| `ATENDENTE` | Handles front-desk operations, including managing patients, rooms, and appointments. Can create `MEDICO` user accounts and register the corresponding professional profile with CRM and specialties. |
| `MEDICO` | Views their own schedule via `GET /consultas/minhas-consultas`. |

**Access principles:**

* All routes require JWT authentication, except `POST /auth/login`.
* Permissions are enforced according to the role required by each operation.
* Patients do not have user accounts, passwords, or tokens.
* Personal schedules are tied to the authenticated doctor's profile.

Specific endpoint permissions are detailed in the tables below.

## Endpoints

All routes below, except `POST /auth/login`, require the following header:

```http
Authorization: Bearer <your-token>

```

### Authentication

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Public | Authenticates a user and returns a JWT. |

### User Accounts

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/usuarios` | `ATENDENTE` to create `MEDICO`; `ADMIN` to create `ATENDENTE` | Creates a user account according to the authenticated user's role. Does not create `ADMIN` profiles or patient accounts. |
| `GET` | `/api/usuarios/{id}` | `ADMIN` | Retrieves account details without returning the password. |

### Rooms

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/salas` | `ADMIN`, `ATENDENTE` | Lists registered rooms. |
| `POST` | `/salas` | `ADMIN`, `ATENDENTE` | Registers a new room. |
| `PUT` | `/salas/{id}` | `ADMIN`, `ATENDENTE` | Updates an existing room. |
| `DELETE` | `/salas/{id}` | `ADMIN`, `ATENDENTE` | Deletes a room with no linked appointments. |

### Doctors

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/medicos` | `ADMIN`, `ATENDENTE` | Lists doctors. |
| `GET` | `/medicos/{id}` | `ADMIN`, `ATENDENTE` | Retrieves a doctor by ID. |
| `POST` | `/medicos` | `ADMIN`, `ATENDENTE` | Creates a doctor profile for an existing account with the `MEDICO` role. |
| `PUT` | `/medicos/{id}` | `ADMIN`, `ATENDENTE` | Updates CRM and specialties while preserving the account link. |
| `DELETE` | `/medicos/{id}` | `ADMIN` | Removes a doctor profile. |

### Patients

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/pacientes` | `ADMIN`, `ATENDENTE` | Lists patients. |
| `GET` | `/pacientes/{id}` | `ADMIN`, `ATENDENTE` | Retrieves a patient by ID. |
| `POST` | `/pacientes` | `ADMIN`, `ATENDENTE` | Registers a patient without creating a user account. |
| `PUT` | `/pacientes/{id}` | `ADMIN`, `ATENDENTE` | Updates patient information. |
| `DELETE` | `/pacientes/{id}` | `ADMIN` | Removes a patient with no linked appointments. |

### Receptionists

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/atendentes` | `ADMIN`, `ATENDENTE` | Lists receptionist profiles. |
| `GET` | `/atendentes/{id}` | `ADMIN`, `ATENDENTE` | Retrieves a receptionist profile by ID. |
| `POST` | `/atendentes` | `ADMIN`, `ATENDENTE` | A receptionist creates their own profile; `ADMIN` creates a profile for an `ATENDENTE` user by providing `usuarioId`. |
| `PUT` | `/atendentes/{id}` | Own profile or `ADMIN` | Updates the profile's department (`setor`). |
| `DELETE` | `/atendentes/{id}` | `ADMIN` | Removes a receptionist profile. |

### Appointments

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/consultas` | `ADMIN`, `ATENDENTE` | Schedules an appointment and checks for time conflicts. |
| `PUT` | `/consultas/{id}` | `ADMIN`, `ATENDENTE` | Updates or reschedules an appointment. |
| `DELETE` | `/consultas/{id}` | `ADMIN`, `ATENDENTE` | Cancels an appointment. |
| `GET` | `/consultas/minhas-consultas` | `MEDICO` | Lists the authenticated doctor's schedule. |

## Setup and Execution

### Prerequisites

Before running the application, make sure you have:

* Java 21;
* MySQL 8;
* Maven Wrapper, included in the repository.

### 1. Configure the Database

Create or provision a MySQL database named `syncspace_db`.

Configure the following environment variables:

| Variable | Description |
| --- | --- |
| `SPRING_DATASOURCE_URL` | MySQL connection URL. |
| `SPRING_DATASOURCE_USERNAME` | Database username. |
| `SPRING_DATASOURCE_PASSWORD` | Database password. |
| `API_SECURITY_TOKEN_SECRET` | Private secret used to sign JWT tokens. |

Example local development setup in PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/syncspace_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME = "your_username"
$env:SPRING_DATASOURCE_PASSWORD = "your_password"
$env:API_SECURITY_TOKEN_SECRET = "your_local_secret"

```

Replace the example values with your local environment credentials. The JWT secret must be strong, private, and compatible with the signing algorithm configured in the application.

**Important:** do not commit real credentials or secrets to the repository. Development configurations should not be reused in production.

### 2. Start the Application

To make exploring the API easier, the `local` profile provides a bootstrap mechanism to create a sample receptionist user.

Configure the credentials for this user:

```powershell
$env:SYNCSPACE_BOOTSTRAP_ATENDENTE_EMAIL = "atendente@clinica.local"
$env:SYNCSPACE_BOOTSTRAP_ATENDENTE_SENHA = "DefineALocalPassword"

```

To also initialize the first local admin account, set the variables below. Do not use these development credentials in production:

```powershell
$env:SYNCSPACE_BOOTSTRAP_ADMIN_EMAIL = "admin@syncspace.local"
$env:SYNCSPACE_BOOTSTRAP_ADMIN_SENHA = "DefineAStrongLocalPassword"

```

Then run the application:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"

```

The `local` profile creates the sample receptionist and, when both `ADMIN` bootstrap variables are set, also creates the local admin user. Bootstrap execution is idempotent and does not overwrite the password of an existing account.

During startup, Flyway runs versioned migrations to prepare or update the database schema.

> **Warning:** the `local` profile and user bootstrap mechanism are intended for development only. Do not enable this profile in production.

### Create a MEDICO Account and Register the Professional Profile

Authenticate as `ATENDENTE`. Using the returned token, create the doctor's user account via `POST /api/usuarios`:

```json
{
  "nome": "Dr. João Silva",
  "email": "joao.silva@syncspace.local",
  "senha": "DefineAStrongPassword",
  "telefone": "11988887777",
  "dataNascimento": "1980-01-01T00:00:00.000Z",
  "cpf": "12345678901",
  "role": "MEDICO"
}

```

The response returns the user ID without exposing the password. Use that ID as `usuarioId` to register the professional profile via `POST /medicos`, also providing the CRM and specialties. A receptionist (`ATENDENTE`) can create patients, appointments, rooms, and `MEDICO` accounts. The local `ADMIN` can create `ATENDENTE` accounts, but not `MEDICO` accounts.

To create a receptionist profile, authenticate as `ADMIN`, create an account with `"role": "ATENDENTE"` via `POST /api/usuarios`, and use the returned ID to create the profile via `POST /atendentes`:

```json
{
  "usuarioId": 12,
  "setor": "ATENDIMENTO"
}

```

## First Usage Flow

The flow below shows how to authenticate a receptionist, register a patient, and schedule an appointment.

### 1. Authenticate

Send a request to `POST /auth/login`:

```http
POST /auth/login
Content-Type: application/json

```

Request body:

```json
{
  "email": "atendente@clinica.local",
  "senha": "DefineALocalPassword"
}

```

The response contains the JWT token:

```json
{
  "token": "<jwt>"
}

```

Use the returned token in the `Authorization` header for subsequent requests.

### 2. Register a Patient

Send a request to `POST /pacientes`:

```http
POST /pacientes
Authorization: Bearer <jwt>
Content-Type: application/json

```

Example body:

```json
{
  "tipoSanguineo": "O_POSITIVO",
  "alergias": "NENHUMA",
  "doencasCronicas": "NENHUMA",
  "medicamentos": "NENHUM",
  "contatoDeEmergencia": "Ana Souza",
  "telefoneContatoEmergencia": "11987654321",
  "prontuario": "PRONT-TESTE-001"
}

```

The example uses placeholder values to show the request payload structure. Sent fields and values must comply with the application's validation rules.

### 3. Schedule an Appointment

With a patient, a room, and a doctor already registered, send a request to `POST /consultas`:

```http
POST /consultas
Authorization: Bearer <jwt>
Content-Type: application/json

```

Example body:

```json
{
  "pacienteId": 1,
  "salaId": 1,
  "medicoId": 8,
  "dataHoraInicio": "2026-11-02T12:00:00.000Z",
  "dataHoraFim": "2026-11-02T13:00:00.000Z"
}

```

IDs are illustrative. Use identifiers that exist in your database and pick an available time slot.

The application validates the requested time range and checks for schedule conflicts involving the patient, doctor, and room.

## Doctors and Specialties

A doctor profile can have multiple specialties. Creating and updating a profile accepts an `especialidades` collection whose values come from the `Especialidade` enum.

Example request:

```json
{
  "usuarioId": 8,
  "crm": "CRM-123456",
  "especialidades": [
    "CARDIOLOGIA",
    "DERMATOLOGIA",
    "ORTOPEDIA"
  ]
}

```

The `usuarioId` field must reference an existing account with the `MEDICO` role.

Specialties are persisted as a collection and returned in the response DTO. The domain collection also prevents duplicate entries in the current implementation.

## Tests

To run the automated test suite, use the Maven Wrapper.

**Windows — PowerShell:**

```powershell
.\mvnw.cmd test

```

**Linux or macOS:**

```bash
./mvnw test

```

Tests use JUnit, Spring Boot Test, and Spring Security Test.

## Next Steps

* [ ] Provide interactive API documentation with Swagger/OpenAPI.
* [ ] Expand unit, authorization, and integration test coverage.

## Project Goal

**SyncSpace** is a portfolio project focused on applying backend development concepts in Java, emphasizing:

* building REST APIs with Spring Boot;
* JWT authentication and role-based authorization;
* entity modeling and relationships with JPA;
* relational persistence and schema versioning with Flyway;
* implementing business rules and domain validations;
* architectural organization, maintainability, and automated testing.

Beyond basic CRUD operations, the goal is to build a consistent foundation for a clinic management system while accounting for data relationships, access permissions, and day-to-day clinical workflows.

---

*SyncSpace — organization, consistency, and security across clinical operations.*
