# 🚦 Smart Queue Management System (Backend)

> 🚀 A backend system designed to manage real-time queues efficiently using Redis, with support for priority handling and estimated waiting time.
---

## 📌 Problem Statement

Traditional queue systems rely heavily on databases, which become inefficient under high-frequency operations. 
This project solves that by using Redis for in-memory queue handling, enabling fast and scalable real-time queue processing.

---

## 🔥 Features

* ⚡ Real-time queue handling using Redis
* 🧑‍💼 Priority queue (VIP users handled first)
* ⏱ Estimated waiting time calculation
* 🔄 FIFO queue management
* ❌ Cancel queue entry
* 📍 Get position in queue
* 📊 Persistent storage using MySQL
* 🧾 Clean REST APIs

---

## 🛠 Tech Stack

* **Backend:** Java, Spring Boot
* **Database:** MySQL
* **Cache / Queue:** Redis
* **ORM:** JPA (Hibernate)

---

## 🏗 System Design

- Redis is used as the primary queue for real-time operations
- MySQL is used for persistent storage and data recovery
- Queue operations (add, next, position) are handled in Redis
- Database is updated only for status tracking (WAITING, SERVING, COMPLETED)

---

## 💡 Key Concepts Implemented

* Redis List operations (`leftPush`, `rightPush`, `leftPop`)
* FIFO queue design
* Priority handling using Redis
* Backend + cache (DB + Redis) hybrid architecture
* Real-time position tracking
* Business logic for waiting time estimation

---

## ⚙️ How It Works

* Users are added to a queue (VIP or normal)
* VIP users are pushed to the front of the queue
* Normal users are added to the end (FIFO)
* Redis maintains the queue for fast operations
* MySQL stores data for persistence
* Position and waiting time are calculated dynamically

---

## ⏱ Waiting Time Logic

Estimated time is calculated as:

```
Estimated Time = (Position - 1) × Time per person
```

(Default: 5 minutes per person)

---

## 📡 Sample APIs

### ➕ Add to Queue

POST /queue?name=John&isVip=false

---

### ▶ Call Next

POST /queue/call-next

---

### 📍 Get Position

GET /queue/position/{id}

---

### ❌ Cancel Entry

DELETE /queue/{id}

---

### 📋 Get All Queue

GET /queue

---

### 🧹 Clear Queue

DELETE /queue/clear

---

## 🚀 Future Improvements

* Distributed queue handling
* Redis persistence (RDB/AOF)
* Notification system (SMS/Email)
* Admin dashboard

---

## ❓ Why Redis?

Using a database for queue operations leads to high latency and increased load under heavy traffic.

Redis provides:
- In-memory storage → faster access
- O(1) push/pop operations
- Efficient handling of high-frequency queue operations

This makes Redis ideal for real-time queue systems.

---

## 💡 Key Learnings

* Implemented real-time queue system using Redis
* Understood difference between in-memory and persistent storage
* Designed backend system with priority handling
* Applied business logic for real-world scenarios

---

## 🙋‍♂️ Author

Srinithi N
| Backend Developer | Java | Spring Boot | Redis
