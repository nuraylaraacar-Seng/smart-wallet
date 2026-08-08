package com.smartwallet.domain.model;

import com.smartwallet.domain.exception.CurrencyMismatchException;
import com.smartwallet.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {

    private static final Currency TRY = Currency.getInstance("TRY");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void shouldDebitWhenBalanceIsSufficient() {
        Wallet wallet = walletWithBalance("100.00");

        wallet.debit(Money.of(new BigDecimal("40.00"), TRY));

        assertEquals(new BigDecimal("60.00"), wallet.getBalance().getAmount());
        assertEquals(1L, wallet.getVersion());
    }

    @Test
    void shouldThrowWhenDebitExceedsBalance() {
        Wallet wallet = walletWithBalance("50.00");
        Money debitAmount = Money.of(new BigDecimal("75.00"), TRY);

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> wallet.debit(debitAmount));

        assertEquals(wallet.getId(), exception.getWalletId());
        assertEquals(new BigDecimal("75.00"), exception.getRequestedAmount());
        assertEquals(new BigDecimal("50.00"), exception.getAvailableAmount());
    }

    @Test
    void shouldCreditMatchingCurrency() {
        Wallet wallet = walletWithBalance("100.00");

        wallet.credit(Money.of(new BigDecimal("25.00"), TRY));

        assertEquals(new BigDecimal("125.00"), wallet.getBalance().getAmount());
        assertEquals(1L, wallet.getVersion());
    }

    @Test
    void shouldThrowWhenCurrencyDoesNotMatchOnDebit() {
        Wallet wallet = walletWithBalance("100.00");

        assertThrows(
                CurrencyMismatchException.class,
                () -> wallet.debit(Money.of(new BigDecimal("10.00"), USD)));
    }

    @Test
    void shouldThrowWhenWalletsHaveDifferentCurrencies() {
        Wallet tryWallet = walletWithBalance("100.00");
        Wallet usdWallet = Wallet.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Money.of(new BigDecimal("100.00"), USD));

        assertThrows(CurrencyMismatchException.class, () -> tryWallet.ensureCurrencyMatches(usdWallet));
    }

    private Wallet walletWithBalance(String amount) {
        return Wallet.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Money.of(new BigDecimal(amount), TRY));
    }
}
