# Helpdesk API

REST API for helpdesk ticket management with role-based access control, SMS/WhatsApp notifications, and full CRUD for users, tickets, and categories.

This project was created for learning purposes, focusing on layered architecture, security, real-world authorization flows, and third-party integrations using Spring Boot.

---

## About the project

This API provides a complete helpdesk backend where clients can open support tickets, support agents can manage and update them, and admins can control users and roles.

Status and priority changes trigger WhatsApp/SMS notifications via Twilio.

All endpoints are protected with JWT Bearer authentication.

The main goal is to practice more advanced backend topics such as Spring Security, OAuth2 resource server, authorization rules, N+1 query optimization, and external API integration.

---

## What you practice here

* REST API design
* Layered architecture
* DTO pattern (Input, Min, Full DTOs)
* Bean Validation with custom error responses
* Role-based authorization with `@PreAuthorize`
* Self-or-admin access rules
* Spring Security with custom password grant
* JWT Bearer authentication
* OAuth2 Resource Server
* JPA with JPQL and join fetch optimizations
* N+1 query prevention
* Global exception handling with `@ControllerAdvice`
* Third-party integration (Twilio SMS/WhatsApp)
* Docker for local infrastructure (PostgreSQL + pgAdmin)
* Environment-based configuration
* Swagger UI (OpenAPI) documentation

---

## Technologies

* Java
* Spring Boot 3.4.5
* Spring Web
* Spring Data JPA
* Spring Security
* OAuth2 Resource Server
* Jakarta Bean Validation
* Twilio SDK
* PostgreSQL
* Docker / Docker Compose
* Maven

---

## Features

**Users**

* Create, update, delete user
* Find user by id
* Find user by name
* List users with pagination
* List users with roles
* Get authenticated user (`/users/me`)

**Tickets**

* Create, update, delete ticket
* Find ticket by id
* List all tickets with pagination
* List tickets with user data
* Search tickets by title
* Search tickets by category
* List tickets ordered by oldest first
* Patch ticket status
* Patch ticket priority

**Categories**

* Create, update, delete category
* Find category by id
* List all categories
* List categories with ticket data

**Roles**

* Find role by id
* List all roles
* List roles with user data

**Notifications**

* Send custom SMS/WhatsApp message via Twilio
* Auto-notify client on ticket status/priority changes

---

## Authorization rules

| Role           | Permissions                                      |
| -------------- | ------------------------------------------------ |
| `ROLE_ADMIN`   | Full access to all resources                     |
| `ROLE_SUPPORT` | Read/update tickets, read users and categories   |
| `ROLE_CLIENT`  | Create tickets, view and manage own tickets only |
| `ROLE_NOC`     | Read-only access for monitoring purposes         |

* Ticket creation automatically binds the ticket to the authenticated user
* Users can only update or delete their own tickets unless they are admins
* Self-or-admin rules are enforced on sensitive operations

---

## Password rules

The password must:

* Have at least 4 characters
* Contain:

  * Uppercase letter
  * Lowercase letter
  * Number
  * Symbol

Passwords are hashed with BCrypt before storage.

---

## Documentation (Swagger)

After starting the application, access:

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Endpoints

Base URL:

```text
http://localhost:8080
```

All endpoints require a JWT Bearer token unless stated otherwise.

### Users

#### Get user by id

```http
GET /users/{id}
```

#### List users

```http
GET /users?page=0&size=10
```

#### List users with roles

```http
GET /users/searchroles?page=0&size=10
```

#### Search users by name

```http
GET /users/search?name=marco
```

#### Get authenticated user

```http
GET /users/me
```

#### Create user

```http
POST /users
```

Request body:

```json
{
  "name": "marco123",
  "email": "marco@email.com",
  "ddd": 11,
  "phone": "998877665",
  "password": "Abc@1234",
  "roles": [{ "id": 1, "authority": "ROLE_CLIENT" }]
}
```

#### Update user

```http
PUT /users/{id}
```

Request body:

```json
{
  "name": "marco123",
  "email": "marco@email.com",
  "ddd": 11,
  "phone": "998877665",
  "password": "Abc@1234",
  "roles": [{ "id": 1, "authority": "ROLE_CLIENT" }]
}
```

#### Delete user

```http
DELETE /users/{id}
```

---

### Tickets

#### Get ticket by id

```http
GET /tickets/{id}
```

#### List tickets

```http
GET /tickets?page=0&size=10
```

#### List tickets with user data

```http
GET /tickets/searchusers?page=0&size=10
```

#### Search tickets by title

```http
GET /tickets/searchtitle?title=internet&page=0&size=10
```

#### Search tickets by category

```http
GET /tickets/searchcategory?category=Connectivity
```

#### List tickets by oldest first

```http
GET /tickets/byoldest?page=0&size=10
```

#### Create ticket

```http
POST /tickets
```

Request body example (recommended for existing categories):

```json
{
  "title": "Internet down",
  "description": "Customer reports no connectivity since morning",
  "categories": [
    { "id": 3 }
  ]
}
```

#### Update ticket

```http
PUT /tickets/{id}
```

#### Patch ticket status

```http
PATCH /tickets/{id}/status
```

Request body:

```json
{
  "status": "IN_PROGRESS"
}
```

Available statuses: `OPEN` - `IN_PROGRESS` - `RESOLVED` - `CLOSED`

#### Patch ticket priority

```http
PATCH /tickets/{id}/priority
```

Request body:

```json
{
  "priority": "HIGH"
}
```

Available priorities: `LOW` - `MEDIUM` - `HIGH`

#### Delete ticket

```http
DELETE /tickets/{id}
```

---

### Categories

#### Get category by id

```http
GET /categories/{id}
```

#### List all categories

```http
GET /categories
```

#### List categories with tickets

```http
GET /categories/searchtickets?page=0&size=10
```

#### Create category

```http
POST /categories
```

Request body:

```json
{
  "name": "Connectivity",
  "description": "Issues related to internet, link and signal"
}
```

#### Update category

```http
PUT /categories/{id}
```

#### Delete category

```http
DELETE /categories/{id}
```

---

### Roles

#### List all roles

```http
GET /roles
```

#### Get role by id

```http
GET /roles/{id}
```

#### List roles with users

```http
GET /roles/searchusers
```

---

### Messages

#### Send custom message

```http
POST /send-message
```

Request body:

```json
{
  "sender": "helpdesk",
  "ddd": 11,
  "phoneNumber": "998877665",
  "message": "Your ticket has been updated."
}
```

---

## Error handling

The API uses centralized exception handling with `@ControllerAdvice`.

### Possible responses

* **400 Bad Request**
  Validation errors, malformed requests

* **401 Unauthorized**
  Missing or invalid JWT token

* **403 Forbidden**
  Authenticated but not allowed to perform the action

* **404 Not Found**
  Resource not found

* **422 Unprocessable Entity**
  Business rule violations (e.g. invalid category on ticket creation)

---

## Project structure

```text
controllers
services
repositories
dto
  user
  ticket
  category
  role
  message
entities
enums
exceptions
security
config
```

---

## Running locally

### Prerequisites

* Java 21+
* Maven
* Docker

### Setup

1. Clone the repository:

```bash
git clone https://github.com/tonicostmarco/helpdesk-spring-api
cd helpdesk-spring-api
```

2. Start the database (PostgreSQL + pgAdmin):

```bash
docker compose up -d
```

3. Configure environment variables.

Example for Docker Compose usage (adjust as needed):

```text
DB_URL=jdbc:postgresql://localhost:5433/mydatabase
DB_USERNAME=postgres
DB_PASSWORD=1234567
```

Twilio (optional in dev):

```text
TWILIO_ACCOUNT_SID=your_sid
TWILIO_AUTH_TOKEN=your_token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
TWILIO_SMS_FROM=+14155238886
```

4. Run the application:

```bash
./mvnw spring-boot:run
```

5. Access the API docs:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Authentication

All endpoints require a JWT Bearer token.

Include it in the `Authorization` header:

```text
Authorization: Bearer <your_token>
```

### Obtaining a token

This project uses an OAuth2 Authorization Server with a custom password grant flow.

Use Swagger UI to find the token endpoint and required parameters.

If you prefer the code reference, check the security configuration for the configured token endpoint and grant parameters.

---

## Notes

* This is a learning-focused project
* Twilio integration is guarded and only initializes when credentials are present
* Docker Compose sets up PostgreSQL and pgAdmin for local development
* The seed file populates initial roles and users for testing
* Some HTTP response codes may vary by endpoint while the API evolves (e.g. 200 vs 201 on create/update)

---

## Next steps

* Write unit and integration tests
* Add refresh token support
* Add ticket assignment to support agents
* Add ticket comment/history tracking
* Deploy to cloud (Railway, Render, or AWS)
