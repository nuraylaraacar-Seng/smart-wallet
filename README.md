```markdown
# Smart Wallet Backend 💳

<img width="2720" height="3600" alt="smart_wallet_full_stack_diagram" src="https://github.com/user-attachments/assets/6a566323-d70a-4e93-8537-4522eec9190b" />

A modular, concurrency-safe digital wallet API built with **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)**. 

While most wallet examples are simple CRUD applications, real-world financial systems require strict data consistency. This project is explicitly engineered to solve complex distributed system problems: preventing double-spending via idempotency, eliminating database deadlocks during concurrent transfers, and securing sensitive user data with envelope encryption.

## System Architecture

<img width="1024" height="806" alt="1201ff2f-0a65-4919-8a8b-e46142397265" src="https://github.com/user-attachments/assets/98599b9e-3c3a-4753-8f97-a890e2abf7b9" />

<img width="3807" height="656" alt="Arc-3-Uml drawio" src="https://github.com/user-attachments/assets/f2caa5ce-fb70-479d-ab45-a92f462439a9" />

<img width="1024" height="1024" alt="3a85f953-5866-4496-8ab6-503cfafff9bd" src="https://github.com/user-attachments/assets/93526d70-3d6f-41c7-aa5c-eaebb33159e6" />

<img width="2720" height="1960" alt="smart_wallet_hexagonal_architecture_final" src="https://github.com/user-attachments/assets/e58c8380-ff8f-4122-ba2d-6be866b279f0" />

## 1. Core Scenarios

### Concurrent Mutual Transfers
**Scenario:** User A transfers money to User B, while User B simultaneously transfers money to User A.
**Flow & Resolution:**
* Both requests hit the `TransferMoneyUseCase`.
* To prevent database deadlocks, the system consistently orders row-level pessimistic locks based on the UUID of the wallets using the internal `WalletLockOrder` utility.
* Balances are mutated, and an atomic transaction is committed.
* An immutable audit log is generated for both debit and credit actions.

### Idempotent Deposits & Withdrawals
**Scenario:** A client network times out, and the user accidentally submits the same deposit request twice.
**Flow & Resolution:**
* Every state-mutating API call requires an `Idempotency-Key` header.
* The system checks the `idempotency_keys` table. 
* If the key exists, it skips the business logic and immediately returns the previously computed `Transaction` entity. Parity and consistency are maintained.

## 2. Concurrency & Resilience Patterns

This project does not rely on simple framework annotations; it tackles race conditions head-on.

### Deadlock-Free Pessimistic Locking
When two wallets interact, locking them in a random order can cause fatal deadlocks. The `WalletLockOrder` guarantees that the wallet with the smaller UUID is always locked first, ensuring a universal locking sequence across all concurrent threads.

```java
// WalletLockOrder.java
static OrderedPair order(UUID firstWalletId, UUID secondWalletId) {
    if (firstWalletId.compareTo(secondWalletId) <= 0) {
        return new OrderedPair(firstWalletId, secondWalletId);
    }
    return new OrderedPair(secondWalletId, firstWalletId);
}

```

### Dual-Write Problem Prevention

After a successful transfer, the system must notify other microservices. To prevent the "Dual-Write" problem (publishing an event to the broker while the DB transaction rolls back), events are strictly bound to the transaction phase:

```java
// TransactionEventListener.java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleTransactionCompleted(TransactionCompletedEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
}

```

## 3. Architecture & DDD

The application strictly follows **Hexagonal Architecture**. Dependencies point inwards: `Infrastructure -> Application -> Domain`.

### Rich Domain Model (Tactical DDD)

The `Wallet` and `Money` classes are not anemic data structures. They encapsulate their own invariant validations and business rules. The domain layer has zero knowledge of Spring, JPA, or external databases.

```java
// Wallet.java (Domain Layer)
public void debit(Money amount) {
    ensureActive();
    ensureSameCurrency(amount);

    if (!balance.isGreaterThanOrEqualTo(amount)) {
        throw new InsufficientBalanceException(id, amount.getAmount(), balance.getAmount(), balance.getCurrency());
    }
    this.balance = balance.subtract(amount);
    this.version++;
}

```

## 4. Security Posture

### Hybrid Envelope Encryption for PII

Sensitive Personally Identifiable Information (like IBANs) is secured using a hybrid approach. The data is encrypted using **AES-256 (GCM)**, and the symmetric AES key is subsequently encrypted using an isolated **RSA-2048** key pair.

### API Protection

* **Asymmetric JWT:** Access and refresh tokens are signed using RS256.
* **Rate Limiting:** Auth endpoints are protected against brute-force attacks via `Bucket4j`, enforcing an IP-based token bucket algorithm.
* **Account Locking:** Managed via Redis; exceeding the maximum failed login threshold temporarily locks the account.

## 5. Tech Stack

* **Core:** Java 21, Spring Boot 3
* **Data Storage:** PostgreSQL (Core & Audit), Redis (Rate Limiting & Auth State)
* **Messaging:** RabbitMQ
* **Security:** JCA (Java Cryptography Architecture), Spring Security, Bucket4j
* **Infrastructure:** Docker, Docker Compose, Flyway

## 6. Quick Start & Installation

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
