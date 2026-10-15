# AI Career Assistant — Week 1

The first working version of the **AI Career Assistant** web application featuring a React Vite frontend, Spring Boot REST API, MySQL database, and secure User Registration & Login flow with BCrypt password hashing.

---

## 🛠️ Technology Stack

- **Frontend**: React 18, Vite, JavaScript, Custom Vanilla CSS (Dark SaaS Glassmorphism design system)
- **Backend**: Java 21, Spring Boot 3.2.3, Spring Web, Spring Data JPA, Hibernate, Spring Security Crypto, Validation
- **Database**: MySQL Server 8.1
- **Build Tools**: Maven 3.9.6, Node.js (v22.9.0) / npm

---

## 📁 Project Structure

```text
E:\AI-Career-Assistant
│
├── backend
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── careerassistant
│   │   │   │           ├── config
│   │   │   │           │   ├── CorsConfig.java
│   │   │   │           │   └── SecurityConfig.java
│   │   │   │           ├── controller
│   │   │   │           │   └── AuthController.java
│   │   │   │           ├── dto
│   │   │   │           │   ├── LoginRequest.java
│   │   │   │           │   ├── LoginResponse.java
│   │   │   │           │   ├── RegisterRequest.java
│   │   │   │           │   └── UserResponse.java
│   │   │   │           ├── entity
│   │   │   │           │   └── User.java
│   │   │   │           ├── exception
│   │   │   │           │   ├── EmailAlreadyExistsException.java
│   │   │   │           │   ├── ErrorResponse.java
│   │   │   │           │   ├── GlobalExceptionHandler.java
│   │   │   │           │   └── InvalidCredentialsException.java
│   │   │   │           ├── repository
│   │   │   │           │   └── UserRepository.java
│   │   │   │           ├── service
│   │   │   │           │   ├── UserService.java
│   │   │   │           │   └── UserServiceImpl.java
│   │   │   │           └── CareerAssistantApplication.java
│   │   │   └── resources
│   │   │       └── application.properties
│   └── pom.xml
│
├── frontend
│   ├── src
│   │   ├── components
│   │   ├── pages
│   │   │   ├── Dashboard.jsx
│   │   │   ├── Login.jsx
│   │   │   └── Register.jsx
│   │   ├── services
│   │   │   └── api.js
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

---

## 📋 Prerequisites

1. **Java JDK 21+**
2. **Node.js v18+ & npm**
3. **MySQL Server 8.0+** running on `localhost:3306`

---

## 🗄️ Database Configuration

1. Make sure MySQL Server is running on `localhost:3306`.
2. Create the database `career_assistant` (Spring Boot is configured with `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE IF NOT EXISTS career_assistant;
```

3. Update `backend/src/main/resources/application.properties` or set environment variables:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/career_assistant?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:YOUR_MYSQL_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080
```

---

## 🚀 How to Run

### 1. Run Backend (Spring Boot REST API)

Open a terminal and execute:

```bash
cd backend
mvn spring-boot:run
```

The Spring Boot REST API will start at: `http://localhost:8080`

---

### 2. Run Frontend (React Vite)

Open a second terminal and execute:

```bash
cd frontend
npm install
npm run dev
```

The React web application will start at: `http://localhost:5173`

---

## 📡 REST API Endpoints

### 1. Register User

- **Method**: `POST`
- **URL**: `/api/auth/register`
- **Request Body**:

```json
{
  "name": "Ramakrishna",
  "email": "ram@example.com",
  "password": "Password123"
}
```

- **Successful Response (201 Created)**:

```json
{
  "id": 1,
  "name": "Ramakrishna",
  "email": "ram@example.com",
  "createdAt": "2026-08-09T06:59:02.1646277"
}
```

- **Error Responses**:
  - `400 Bad Request`: Validation failure (empty field or invalid email format)
  - `409 Conflict`: `{"message": "Email already registered"}`

---

### 2. Login User

- **Method**: `POST`
- **URL**: `/api/auth/login`
- **Request Body**:

```json
{
  "email": "ram@example.com",
  "password": "Password123"
}
```

- **Successful Response (200 OK)**:

```json
{
  "message": "Login successful",
  "user": {
    "id": 1,
    "name": "Ramakrishna",
    "email": "ram@example.com",
    "createdAt": "2026-08-09T06:59:02.1646277"
  }
}
```

- **Error Responses**:
  - `401 Unauthorized`: `{"message": "Invalid email or password"}`

---

## 🧪 How to Test

### Automated PowerShell API Test

```powershell
# Register
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body '{"name":"Ramakrishna","email":"ram@example.com","password":"Password123"}'

# Login
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"ram@example.com","password":"Password123"}'
```

### Manual Testing via React Frontend

1. Navigate to `http://localhost:5173`.
2. Click **Create one** to open the Registration page.
3. Fill out full name, email, password, and confirm password.
4. Click **Create Account** -> view success message -> auto-redirect to Login page.
5. Log in with registered credentials -> view Dashboard showing personalized greeting (`Welcome, {user.name}`) and upcoming roadmap cards.

---

## 🔮 Future Modules (Week 2+)

- **Resume Analysis** (PDF processing, skill extraction)
- **Career Recommendation** (AI career path matching)
- **Job Matching** (Automated alignment scoring)
- **AI Interview** (Interactive real-time interview practice)
