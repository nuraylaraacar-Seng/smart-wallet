package com.smartwallet.application.usecase;

import java.util.UUID;

final class WalletLockOrder {

    record OrderedPair(UUID first, UUID second) {
    }

    private WalletLockOrder() {
    }

    static OrderedPair order(UUID firstWalletId, UUID secondWalletId) {
        if (firstWalletId.compareTo(secondWalletId) <= 0) {
            return new OrderedPair(firstWalletId, secondWalletId);
        }
        return new OrderedPair(secondWalletId, firstWalletId);
    }
}
