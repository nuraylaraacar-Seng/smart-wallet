package com.smartwallet.infrastructure.adapter.in.web;

import org.springframework.security.core.Authentication;
import com.smartwallet.application.port.in.DepositCommand;
import com.smartwallet.application.port.in.DepositUseCase;
import com.smartwallet.application.port.in.TransferMoneyCommand;
import com.smartwallet.application.port.in.TransferMoneyUseCase;
import com.smartwallet.application.port.in.WithdrawCommand;
import com.smartwallet.application.port.in.WithdrawUseCase;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import com.smartwallet.domain.model.Wallet;
import com.smartwallet.infrastructure.adapter.out.persistence.SpringDataTransactionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final SpringDataTransactionRepository transactionRepository;
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final DepositUseCase depositUseCase;
    private final WalletRepositoryPort walletRepository;

    public WalletController(
            TransferMoneyUseCase transferMoneyUseCase,
            WithdrawUseCase withdrawUseCase,
            DepositUseCase depositUseCase,
            WalletRepositoryPort walletRepository,
            SpringDataTransactionRepository transactionRepository
    ) {
        this.transferMoneyUseCase = transferMoneyUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.depositUseCase = depositUseCase;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Map<String, Object>> getWallet(
            @PathVariable UUID walletId,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Wallet not found: " + walletId));

        if (!wallet.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Bu cüzdana erişim yetkiniz yok.");
        }

        return ResponseEntity.ok(
                Map.of(
                        "walletId", wallet.getId(),
                        "balance", wallet.getBalance().getAmount(),
                        "currency", wallet.getBalance().getCurrency().getCurrencyCode()
                )
        );
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @PathVariable UUID walletId) {

        List<TransactionResponse> transactions = transactionRepository.findAll()
                .stream()
                .filter(tx ->
                        walletId.equals(tx.getSourceWalletId()) ||
                                walletId.equals(tx.getTargetWalletId()))
                .sorted((a, b) ->
                        b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAmountValue(),
                        tx.getAmountCurrency(),
                        tx.getType(),
                        tx.getStatus(),
                        tx.getCreatedAt(),
                        walletId.equals(tx.getTargetWalletId()) ? "IN" : "OUT"
                ))
                .toList();

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{walletId}/transfer")
    public ResponseEntity<Transaction> transfer(
            @PathVariable UUID walletId,
            @Valid @RequestBody TransferApiRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        UUID targetId = request.targetWalletId();

        // Eğer targetWalletId gönderilmediyse veya null ise hata fırlatmak yerine güvenli kontrol
        if (targetId == null) {
            throw new IllegalArgumentException("Alıcı cüzdan bilgisi (targetWalletId) eksik.");
        }

        Money money = Money.of(request.amount(), request.currency());
        TransferMoneyCommand command = new TransferMoneyCommand(walletId, targetId, money, idempotencyKey);

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
            UUID targetWalletId,
            String targetEmail,
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}

    public record DepositApiRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}

    public record WithdrawApiRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}

    public record TransactionResponse(
            UUID id,
            BigDecimal amount,
            String currency,
            String type,
            String status,
            Instant createdAt,
            String direction
    ) {}
}