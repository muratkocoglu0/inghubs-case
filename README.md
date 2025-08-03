# Brokerage Firm Backend

This project is a **Spring Boot** backend service designed for a brokerage firm.  
It allows employees to **place, list, and cancel stock orders** on behalf of their customers, while ensuring **balance management** and **transactional consistency**.

---

## Features
- **JWT Authentication** for secure login
- **Role-based authorization**
- **Customer-specific asset management**
- **Balance reservation** for buy orders
- **Concurrency-safe operations**
- **Global Exception Handling**
- **Entity ↔ DTO mapping** with MapStruct
- **Boilerplate reduction** using Lombok

---

## Tech Stack
- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA (Hibernate)**
- **Spring Security + JWT**
- **Lombok**
- **MapStruct**
- **H2 Database** (for testing)
- **Maven**

---

## Installation

1. Clone the repository:
```bash
git clone https://github.com/muratkocoglu0/inghubs-case.git
cd inghubs-case