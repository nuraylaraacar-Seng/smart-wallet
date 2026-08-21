# smart-wallet

<img width="2720" height="3600" alt="smart_wallet_full_stack_diagram" src="https://github.com/user-attachments/assets/6a566323-d70a-4e93-8537-4522eec9190b" />
 
## System Architecture 

<img width="1024" height="806" alt="1201ff2f-0a65-4919-8a8b-e46142397265" src="https://github.com/user-attachments/assets/98599b9e-3c3a-4753-8f97-a890e2abf7b9" />


<img width="3807" height="656" alt="Arc-3-Uml drawio" src="https://github.com/user-attachments/assets/f2caa5ce-fb70-479d-ab45-a92f462439a9" />

<img width="1024" height="1024" alt="3a85f953-5866-4496-8ab6-503cfafff9bd" src="https://github.com/user-attachments/assets/93526d70-3d6f-41c7-aa5c-eaebb33159e6" />


<img width="2720" height="1960" alt="smart_wallet_hexagonal_architecture_final" src="https://github.com/user-attachments/assets/e58c8380-ff8f-4122-ba2d-6be866b279f0" />


Haklısın, kafanın karışması çok normal. İki formatın da hizmet ettiği amaç farklıydı ama en profesyonel olanı kesinlikle ikisinin **"hibrit"** bir versiyonunu oluşturmak.

Sektörde işe alım süreçleri genellikle iki aşamalıdır: İlk aşamada İK uzmanı repoya girip saniyeler içinde teknolojilere ve kurulum adımlarına bakar (ikinci şablonun gücü). İkinci aşamada ise teknik lider kodu inceler ve çözdüğün problemleri okur (senin beğendiğin ilk şablonun gücü). Dağıtık sistemler, concurrency ve veri tutarlılığı gibi konular şirketlerin bütçe ayırdığı ve yetenek aradığı asıl alanlardır. Bu yüzden bu detayları kaybetmememiz harika bir karar.

İki dünyanın en iyi yanlarını (derin mühendislik hikayesi + görseller + hızlı kurulum) birleştirdiğim nihai ve dengeli taslağı aşağıda hazırladım:

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

```

***

Bu versiyon, İK uzmanına en alttaki kurulum ve teknoloji kısmıyla hızlıca aradığını verirken, teknik mülakatçıya da yukarıdaki kod blokları ve senaryolarla mühendislik derinliğini gösterir. Mülakat masasına oturduğunda, doğrudan bu README üzerinden teknik tartışmalar yürütebilirsin.

Denge şimdi sence nasıl oldu? Şemaları eklerken Markdown tarafında yardıma ihtiyacın olacak mı?

```
