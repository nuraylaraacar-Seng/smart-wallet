package com.smartwallet.application.usecase;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletLockOrderTest {

    @Test
    void shouldOrderWalletIdsAscending() {
        UUID lowerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID higherId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        WalletLockOrder.OrderedPair orderedPair = WalletLockOrder.order(higherId, lowerId);

        assertEquals(lowerId, orderedPair.first());
        assertEquals(higherId, orderedPair.second());
    }
}
