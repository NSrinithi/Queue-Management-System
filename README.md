# 🚦 Smart Queue Management System (Backend)

> 🚀 A production-oriented backend system for real-time queue management using **Redis + MySQL**, with priority handling, testing, and Dockerized deployment.

---

## 📌 Overview

This project implements a **high-performance queue management system** designed to handle real-time operations efficiently.

Instead of relying solely on traditional databases, it uses **Redis (in-memory)** for fast queue operations and **MySQL** for persistence — achieving both **speed and reliability**.

---

## 🔥 Key Features

* ⚡ Real-time queue operations using Redis
* 🧑‍💼 VIP priority handling (priority queue logic)
* ⏱ Dynamic waiting time estimation
* 🔄 FIFO queue management
* ❌ Cancel queue entries
* 📍 Get real-time queue position
* 📊 Persistent storage with MySQL
* 🧾 Clean RESTful APIs
* 🧪 Comprehensive testing (Unit + Controller + Integration)
* 🐳 Fully Dockerized (App + MySQL + Redis)

---

## 🛠 Tech Stack

| Layer       | Technology                |
| ----------- | ------------------------- |
| Backend     | Java, Spring Boot         |
| Database    | MySQL                     |
| Cache/Queue | Redis                     |
| ORM         | JPA (Hibernate)           |
| Testing     | JUnit 5, Mockito, MockMvc |
| DevOps      | Docker, Docker Compose    |

---

## 🏗 Architecture

```
Client
   ↓
Spring Boot Application
   ↓
-----------------------------
| Redis (Queue Operations)  |
| MySQL (Persistent Data)   |
-----------------------------
```

### ⚙️ Design Decisions

* **Redis** → Handles queue operations (O(1) push/pop)
* **MySQL** → Stores user data & status (WAITING, SERVING, COMPLETED)
* **Hybrid approach** ensures:

  * High performance (Redis)
  * Data safety (MySQL)

---

## 🔄 System Flow

1. User joins queue (VIP / Normal)
2. VIP → added to front (`leftPush`)
3. Normal → added to end (`rightPush`)
4. Redis maintains queue order
5. MySQL tracks user status
6. System calculates position & waiting time dynamically

---

## ⏱ Waiting Time Logic

```
Estimated Time = (Position - 1) × Time per person
```

(Default: 5 minutes per person)

---

## 🧪 Testing Strategy

Implemented a **multi-layered testing approach**:

### ✅ Unit Testing (Service Layer)

* JUnit 5 + Mockito
* Mocked:

  * Repository
  * RedisTemplate
* Covered:

  * Success scenarios
  * Failure cases (DB down, Redis failure)
  * Edge cases (null input, empty queue)

### ✅ Advanced Testing Techniques

* `ArgumentCaptor` → verify DB data
* `InOrder` → verify execution flow
* `verifyNoInteractions` → strict validation

### ✅ Controller Testing

* MockMvc for API testing
* Verified:

  * HTTP status codes
  * JSON responses
  * Exception handling

### ✅ Integration Testing

* `@SpringBootTest` full flow testing
* Simulated Redis behavior for queue operations

---

## 🐳 Docker Setup

### 🔧 Dockerfile

* Multi-stage build (Maven + lightweight runtime)
* Optimized image size
* Production-ready setup

### 🧩 Docker Compose

Runs entire system with one command:

* Spring Boot App
* MySQL
* Redis

```bash
docker compose up
```

### Key Features:

* Service-based networking (`mysql`, `redis`)
* Health checks for DB readiness
* Volume support for persistence

---

## 📡 API Endpoints

### ➕ Add to Queue

```
POST /queue/join
```

### ▶ Call Next

```
GET /queue/next
```

### 📍 Get Position

```
GET /queue/position?id={id}
```

### ❌ Cancel Entry

```
DELETE /queue/cancel?id={id}
```

### 👤 Current User

```
GET /queue/current
```

---

## 🚀 How to Run

### 🟢 Using Docker (Recommended)

```bash
docker compose up
```

---

### 🟡 Local Setup

1. Start MySQL & Redis
2. Run Spring Boot app:

```bash
mvn spring-boot:run
```

---

## 💡 Why Redis?

Traditional DB-based queues:

* ❌ Slow under high load
* ❌ High latency

Redis provides:

* ✅ In-memory operations → ultra-fast
* ✅ O(1) push/pop
* ✅ Ideal for real-time systems

---

## 🔥 Key Engineering Highlights

* Hybrid architecture (Redis + DB)
* Real-time system design
* Failure scenario handling
* Layered testing strategy
* Dockerized full-stack setup
* Debugging real-world issues

---

## 🙋‍♂️ Author

**Srinithi N**
Backend Developer | Java | Spring Boot | Redis
