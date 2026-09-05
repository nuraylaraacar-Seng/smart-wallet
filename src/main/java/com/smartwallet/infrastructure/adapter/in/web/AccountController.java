package com.smartwallet.infrastructure.adapter.in.web;

import com.smartwallet.application.port.in.TransferMoneyCommand;
import com.smartwallet.application.port.in.TransferMoneyUseCase;
import com.smartwallet.application.port.out.IbanEncryptionPort;
import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.User;
import com.smartwallet.domain.model.Wallet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final UserRepositoryPort userRepository;
    private final WalletRepositoryPort walletRepository;
    private final IbanEncryptionPort ibanEncryptionPort;
    private final TransferMoneyUseCase transferMoneyUseCase;

    public AccountController(UserRepositoryPort userRepository, WalletRepositoryPort walletRepository,
                             IbanEncryptionPort ibanEncryptionPort, TransferMoneyUseCase transferMoneyUseCase) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.ibanEncryptionPort = ibanEncryptionPort;
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getMyAccount(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet newWallet = Wallet.create(UUID.randomUUID(), userId, Money.of(BigDecimal.ZERO, "TRY"));
                    return walletRepository.save(newWallet);
                });

        String decryptedIban = ibanEncryptionPort.decrypt(user.getEncryptedIban());

        return ResponseEntity.ok(new AccountResponse(
                wallet.getId(),
                user.getEmail(),
                decryptedIban,
                wallet.getBalance().getAmount(),
                wallet.getBalance().getCurrency().getCurrencyCode()
        ));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferByEmail(
            Authentication authentication,
            @Valid @RequestBody TransferByEmailRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        UUID sourceUserId = (UUID) authentication.getPrincipal();
        Wallet sourceWallet = walletRepository.findByUserId(sourceUserId)
                .orElseThrow(() -> new IllegalArgumentException("Cüzdanınız bulunamadı."));

        User targetUser = userRepository.findByEmail(request.targetEmail())
                .orElseThrow(() -> new IllegalArgumentException("Alıcı e-posta adresi sistemde bulunamadı."));
        Wallet targetWallet = walletRepository.findByUserId(targetUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Alıcının aktif bir cüzdanı yok."));

        Money money = Money.of(request.amount(), request.currency());
        TransferMoneyCommand command = new TransferMoneyCommand(sourceWallet.getId(), targetWallet.getId(), money, idempotencyKey);

        transferMoneyUseCase.transfer(command);
        return ResponseEntity.ok().build();
    }

    public record AccountResponse(
            UUID walletId,
            String email,
            String iban,
            BigDecimal balance,
            String currency) {
    }

    public record TransferByEmailRequest(@NotBlank String targetEmail, @NotNull @Positive BigDecimal amount,
                                         @NotBlank String currency) {
    }
}