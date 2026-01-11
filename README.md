# 🏦 Banking Application – Spring Boot

A **secure RESTful Banking Application** built using **Spring Boot** that provides core banking operations such as user registration, account management, transactions, and statement generation.  
The application uses **JWT-based authentication**, supports **email notifications**, and can generate **PDF bank statements**.

---

## 🚀 Features

- User Registration & Login
- JWT Authentication & Authorization
- Create and manage bank accounts
- Deposit & withdraw money
- Perform transactions
- View transaction history
- Generate bank statements in PDF format
- Email notifications
- Asynchronous processing
- Secure API endpoints

---

## 🛠️ Tech Stack

- **Java**
- **Spring Boot**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Spring Data JPA**
- **Hibernate**
- **Maven**
- **REST APIs**
- **PDF generation**
- **Email service**

---

## 🔐 Authentication

- Uses **JWT (JSON Web Tokens)** for securing REST APIs
- Stateless authentication mechanism
- Custom authentication filter and authentication entry point
- Token validation performed on every request

---

## 📄 PDF Statement Generation

- Users can generate **bank statements in PDF format**
- PDF includes detailed transaction history
- Useful for reports, audits, and record keeping

---

## 📧 Email Service

- Sends email notifications to users
- Used for alerts, confirmations, and statement delivery

---

## ▶️ How to Run the Application

### ✅ Prerequisites

- Java 11 or higher
- Maven

---

### 🚀 Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Viji-C/Banking-App.git

2. **Navigate to the project directory**
   ```bash
   cd Banking-App

3. **Build and run the application**
   ```bash
   mvn spring-boot:run

4. **Access the application in your browser**
   ```bash
   http://localhost:8082
