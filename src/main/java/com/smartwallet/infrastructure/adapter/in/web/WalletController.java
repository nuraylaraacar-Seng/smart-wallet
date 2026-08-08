package com.smartwallet.infrastructure.adapter.in.web;

import com.smartwallet.application.port.in.DepositCommand;
import com.smartwallet.application.port.in.DepositUseCase;
import com.smartwallet.application.port.in.TransferMoneyCommand;
import com.smartwallet.application.port.in.TransferMoneyUseCase;
import com.smartwallet.application.port.in.WithdrawCommand;
import com.smartwallet.application.port.in.WithdrawUseCase;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final TransferMoneyUseCase transferMoneyUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final DepositUseCase depositUseCase;

    public WalletController(
            TransferMoneyUseCase transferMoneyUseCase,
            WithdrawUseCase withdrawUseCase,
            DepositUseCase depositUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.depositUseCase = depositUseCase;
    }

    @PostMapping("/{walletId}/transfer")
    public ResponseEntity<Transaction> transfer(
            @PathVariable UUID walletId,
            @Valid @RequestBody TransferApiRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        Money money = Money.of(request.amount(), request.currency());
        TransferMoneyCommand command = new TransferMoneyCommand(walletId, request.targetWalletId(), money, idempotencyKey);

        Transaction transaction = transferMoneyUseCase.transfer(command);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<Transaction> deposit(
            @PathVariable UUID walletId,
            @Valid @RequestBody DepositApiRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        Money money = Money.of(request.amount(), request.currency());
        DepositCommand command = new DepositCommand(walletId, money, idempotencyKey);

        Transaction transaction = depositUseCase.deposit(command);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<Transaction> withdraw(
            @PathVariable UUID walletId,
            @Valid @RequestBody WithdrawApiRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        Money money = Money.of(request.amount(), request.currency());
        WithdrawCommand command = new WithdrawCommand(walletId, money, idempotencyKey);

        Transaction transaction = withdrawUseCase.withdraw(command);
        return ResponseEntity.ok(transaction);
    }

    public record TransferApiRequest(
            @NotNull UUID targetWalletId,
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}

    public record DepositApiRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}

    public record WithdrawApiRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}
}