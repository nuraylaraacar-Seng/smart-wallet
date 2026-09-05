package com.smartwallet.application.usecase;

import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public class InternalLiquidityService {

    public static final UUID MASTER_WALLET_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final WalletRepositoryPort walletRepository;

    public InternalLiquidityService(WalletRepositoryPort walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void fundUserWallet(UUID targetWalletId, BigDecimal amount, String currency) {
        executeTransfer(targetWalletId, amount, currency);
    }

    public void manualAdminTransfer(UUID targetWalletId, BigDecimal amount, String currency) {
        executeTransfer(targetWalletId, amount, currency);
    }

    private void executeTransfer(UUID targetWalletId, BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        WalletLockOrder.OrderedPair lockOrder = WalletLockOrder.order(MASTER_WALLET_ID, targetWalletId);

        Wallet firstWallet = walletRepository.findByIdWithLock(lockOrder.first())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + lockOrder.first()));
        Wallet secondWallet = walletRepository.findByIdWithLock(lockOrder.second())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + lockOrder.second()));

        Wallet masterWallet = MASTER_WALLET_ID.equals(lockOrder.first()) ? firstWallet : secondWallet;
        Wallet targetWallet = targetWalletId.equals(lockOrder.first()) ? firstWallet : secondWallet;

        Money transferMoney = Money.of(amount, currency);

        masterWallet.debit(transferMoney);
        targetWallet.credit(transferMoney);

        walletRepository.save(masterWallet);
        walletRepository.save(targetWallet);
    }
}