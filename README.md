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
