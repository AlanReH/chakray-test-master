# Chakray REST API

## Description

REST API built with Spring Boot (JDK 21, Maven) that manages users in memory.

---

## Run the project

```bash
mvn spring-boot:run
```

App runs on:

```
http://localhost:8080
```

Swagger
```
http://localhost:8080/swagger-ui/index.html
```

---

## Endpoints

### GET /users

Get all users

Optional query params:

* `sortedBy`: email | id | name | phone | tax_id | created_at
* `filter`: field+condition+value

Examples:

```
/users?sortedBy=name
/users?filter=name+co+user
```

---

### POST /users

Create a new user

```json
{
  "email": "test@mail.com",
  "name": "test",
  "phone": "+525512345678",
  "password": "123456",
  "tax_id": "TEST990101ABC"
}
```

---

### PATCH /users/{id}

Update user partially

```json
{
  "email": "updated@mail.com"
}
```

---

### DELETE /users/{id}

Delete user by ID

---

### POST /api/login

Authenticate user

```json
{
  "tax_id": "AARR990101XXX",
  "password": "123456"
}
```
