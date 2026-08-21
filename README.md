```markdown
# Smart Wallet Backend

A digital wallet API focused on data consistency and concurrency control. It's built with Hexagonal Architecture (Ports & Adapters) and Domain-Driven Design (DDD) to handle real-world financial system problems like double-spending, database deadlocks during concurrent transfers, and securing sensitive user data.

---

## 1. Concurrency & Data Integrity

### Deadlock-Safe Mutual Transfers
During concurrent transfers between two wallets (e.g., A -> B and B -> A simultaneously), acquiring database locks in a random order causes deadlocks. The system enforces a strict locking order using `WalletLockOrder` by comparing wallet UUIDs before executing row-level pessimistic locks.

```java
// WalletLockOrder.java
static OrderedPair order(UUID firstWalletId, UUID secondWalletId) {
    if (firstWalletId.compareTo(secondWalletId) <= 0) {
        return new OrderedPair(firstWalletId, secondWalletId);
    }
    return new OrderedPair(secondWalletId, firstWalletId);
}

```

### Idempotent Operations

To handle network timeouts and prevent double-spending, every state-mutating API call requires an `Idempotency-Key` header. The system checks the `idempotency_keys` table before processing. If a duplicate request is detected, it returns the previously computed `Transaction` entity without re-executing the logic.

### Preventing Dual-Write Issues

When a transfer is successful, an event is published to the message broker. To avoid the dual-write problem (e.g., publishing the message but the DB transaction rolling back), events are strictly bound to the `AFTER_COMMIT` phase.

```java
// TransactionEventListener.java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleTransactionCompleted(TransactionCompletedEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
}

```

---

## 2. Architecture & Security

### Hexagonal Architecture & DDD

The application enforces the dependency rule: `Infrastructure -> Application -> Domain`. The `Wallet` and `Money` classes act as rich domain models that handle their own invariant validations (like currency matching and negative balance checks) entirely independent of Spring or JPA.

### Security Details

* **Envelope Encryption:** Sensitive PII (like IBANs) are encrypted using AES-256 (GCM). The symmetric AES key is then encrypted using an isolated RSA-2048 key pair.
* **API Protection:** Authentication uses asymmetric JWT (RS256). Auth endpoints are protected against brute-force attacks via `Bucket4j` (token bucket algorithm) and Redis-backed temporary account locking.

---

## 3. Tech Stack

* **Core:** Java 21, Spring Boot 3
* **Data Storage:** PostgreSQL (Core & Audit), Redis (Rate Limiting & Auth State)
* **Messaging:** RabbitMQ
* **Security:** Java Cryptography Architecture (JCA), Spring Security, Bucket4j
* **Infrastructure:** Docker, Docker Compose, Flyway

---

## 4. Quick Start & Installation

To run this project locally, you need Docker and Docker Compose installed.

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

```

```
