package com.smartwallet.domain.model;

import com.smartwallet.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    private static final Currency TRY = Currency.getInstance("TRY");

    @Test
    void shouldCreateMoneyWithValidAmount() {
        Money money = Money.of(new BigDecimal("100.50"), TRY);

        assertEquals(new BigDecimal("100.50"), money.getAmount());
        assertEquals(TRY, money.getCurrency());
    }

    @Test
    void shouldRejectNegativeAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Money.of(new BigDecimal("-1.00"), TRY));

        assertTrue(exception.getMessage().contains("negative"));
    }

    @Test
    void shouldAddSameCurrencyAmounts() {
        Money first = Money.of(new BigDecimal("100.00"), TRY);
        Money second = Money.of(new BigDecimal("25.50"), TRY);

        Money result = first.add(second);

        assertEquals(new BigDecimal("125.50"), result.getAmount());
    }

    @Test
    void shouldThrowWhenAddingDifferentCurrencies() {
        Money tryMoney = Money.of(new BigDecimal("100.00"), TRY);
        Money usdMoney = Money.of(new BigDecimal("50.00"), Currency.getInstance("USD"));

        assertThrows(CurrencyMismatchException.class, () -> tryMoney.add(usdMoney));
    }

    @Test
    void shouldSubtractWhenResultIsNonNegative() {
        Money first = Money.of(new BigDecimal("100.00"), TRY);
        Money second = Money.of(new BigDecimal("40.00"), TRY);

        Money result = first.subtract(second);

        assertEquals(new BigDecimal("60.00"), result.getAmount());
    }

    @Test
    void shouldRejectSubtractionThatWouldBeNegative() {
        Money first = Money.of(new BigDecimal("10.00"), TRY);
        Money second = Money.of(new BigDecimal("20.00"), TRY);

        assertThrows(IllegalArgumentException.class, () -> first.subtract(second));
    }
}
