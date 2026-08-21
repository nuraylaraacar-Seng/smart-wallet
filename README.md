---

```markdown
# Smart Wallet Backend 💳

A modular, concurrency-safe digital wallet API built with **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)**. 

While most wallet examples are simple CRUD applications, real-world financial systems require strict data consistency. This project is explicitly engineered to solve complex distributed system problems: preventing double-spending via idempotency, eliminating database deadlocks during concurrent transfers, and securing sensitive user data with envelope encryption.

> **[📝 NOT: Draw.io ile hazırladığın Hexagonal Architecture veya Genel Sistem şemasını tam bu alana görsel olarak eklemelisin.]**
> `<img width="800" alt="system-architecture" src="https://github.com/user-attachments/assets/senin-gorsel-linkin" />`

---

## 1. Core Scenarios & Engineering Decisions

This project does not rely on simple framework annotations; it tackles race conditions head-on.

### Concurrent Mutual Transfers (Deadlock Prevention)
**Scenario:** User A transfers money to User B, while User B simultaneously transfers money to User A.
**Resolution:** To prevent database deadlocks, the system consistently orders row-level pessimistic locks based on the UUID of the wallets. The `WalletLockOrder` utility guarantees that the wallet with the smaller UUID is always locked first.

```java
// WalletLockOrder.java
static OrderedPair order(UUID firstWalletId, UUID secondWalletId) {
    if (firstWalletId.compareTo(secondWalletId) <= 0) {
        return new OrderedPair(firstWalletId, secondWalletId);
    }
    return new OrderedPair(secondWalletId, firstWalletId);
}

```

### Idempotent Deposits & Withdrawals

**Scenario:** A client network times out, and the user accidentally submits the same deposit request twice.
**Resolution:** Every state-mutating API call requires an `Idempotency-Key` header. The system checks the `idempotency_keys` table. If the key exists, it skips the business logic and immediately returns the previously computed `Transaction` entity.

### Dual-Write Problem Prevention

After a successful transfer, the system publishes an event. To prevent the "Dual-Write" problem (publishing an event while the DB transaction rolls back), events are strictly bound to the `AFTER_COMMIT` phase.

```java
// TransactionEventListener.java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleTransactionCompleted(TransactionCompletedEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
}

```

---

## 2. Architecture & Security

**Hexagonal Architecture & DDD**
The application strictly enforces the dependency rule: `Infrastructure -> Application -> Domain`.
The `Wallet` and `Money` classes are rich domain models, encapsulating invariant validations (e.g., currency matching, negative balance checks) without any Spring or JPA dependencies.

**Security Posture**

* **Envelope Encryption (PII):** IBANs are encrypted using AES-256 (GCM), and the symmetric AES key is encrypted using an isolated RSA-2048 key pair.
* **API Protection:** Asymmetric JWT (RS256) for authentication. Auth endpoints are protected against brute-force attacks via `Bucket4j` (Token Bucket Algorithm) and Redis-backed account locking.

---

## 3. Tech Stack

* **Core:** Java 21, Spring Boot 3
* **Data Storage:** PostgreSQL (Core & Audit), Redis (Rate Limiting & Auth State)
* **Messaging:** RabbitMQ
* **Security:** Java Cryptography Architecture (JCA), Spring Security, Bucket4j
* **Infrastructure:** Docker, Docker Compose, Flyway

---

## 4. Quick Start & Installation

To run this project locally, you need **Docker** and **Docker Compose** installed.

**1. Clone the repository:**

```bash
git clone [https://github.com/nuraylaraacar-Seng/smart-wallet.git](https://github.com/nuraylaraacar-Seng/smart-wallet.git)
cd smart-wallet

```

**2. Start the infrastructure (PostgreSQL, Redis, RabbitMQ):**

```bash
docker-compose up -d

```

**3. Run the application:**

```bash
./mvnw spring-boot:run

```

*The API will be available at `http://localhost:8080/api/v1/`.*

