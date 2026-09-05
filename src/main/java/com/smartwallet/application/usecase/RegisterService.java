package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.RegisterCommand;
import com.smartwallet.application.port.in.RegisterUseCase;
import com.smartwallet.application.port.out.IbanEncryptionPort;
import com.smartwallet.application.port.out.PasswordHasherPort;
import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.exception.EmailAlreadyExistsException;
import com.smartwallet.domain.model.EncryptedIban;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.User;
import com.smartwallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepository;
    private final WalletRepositoryPort walletRepository;
    private final PasswordHasherPort passwordHasher;
    private final IbanEncryptionPort ibanEncryptionPort;
    private final InternalLiquidityService internalLiquidityService;

    public RegisterService(
            UserRepositoryPort userRepository,
            WalletRepositoryPort walletRepository,
            PasswordHasherPort passwordHasher,
            IbanEncryptionPort ibanEncryptionPort,
            InternalLiquidityService internalLiquidityService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordHasher = passwordHasher;
        this.ibanEncryptionPort = ibanEncryptionPort;
        this.internalLiquidityService = internalLiquidityService;
    }

    @Override
    public User register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());
        UUID userId = UUID.randomUUID();
        User user = User.register(userId, command.email(), passwordHash);

        String generatedIban = generateRandomIban();
        EncryptedIban encryptedIban = ibanEncryptionPort.encrypt(generatedIban);
        user = user.withEncryptedIban(encryptedIban);
        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.create(UUID.randomUUID(), savedUser.getId(), Money.of(BigDecimal.ZERO, "TRY"));
        walletRepository.save(wallet);

        internalLiquidityService.fundUserWallet(wallet.getId(), new BigDecimal("1000.00"), "TRY");

        return savedUser;
    }

    private String generateRandomIban() {
        StringBuilder iban = new StringBuilder("TR");
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            iban.append(random.nextInt(10));
        }
        return iban.toString();
    }
}