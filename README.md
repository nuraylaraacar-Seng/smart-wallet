# Smart Wallet Backend

A digital wallet API focused on data consistency and concurrency control. It's built with Hexagonal Architecture (Ports & Adapters) and Domain-Driven Design (DDD) to handle real-world financial system problems like double-spending, database deadlocks during concurrent transfers, and securing sensitive user data.

---

## 1. Concurrency & Data Integrity

### Deadlock-Safe Mutual Transfers
During concurrent transfers between two wallets (e.g., A -> B and B -> A simultaneously), acquiring database locks in a random order causes deadlocks. The system enforces a strict locking order using `WalletLockOrder` by comparing wallet UUIDs before executing row-level pessimistic locks.


// WalletLockOrder.java
static OrderedPair order(UUID firstWalletId, UUID secondWalletId) {
    if (firstWalletId.compareTo(secondWalletId) <= 0) {
        return new OrderedPair(firstWalletId, secondWalletId);
    }
    return new OrderedPair(secondWalletId, firstWalletId);
}

Idempotent Operations
To handle network timeouts and prevent double-spending, every state-mutating API call requires an Idempotency-Key header. The system checks the idempotency_keys table before processing. If a duplicate request is detected, it returns the previously computed Transaction entity without re-executing the logic.

Preventing Dual-Write Issues
When a transfer is successful, an event is published to the message broker. To avoid the dual-write problem (e.g., publishing the message but the DB transaction rolling back), events are strictly bound to the AFTER_COMMIT phase.

