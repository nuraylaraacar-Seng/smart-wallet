package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.RegisterCommand;
import com.smartwallet.application.port.in.RegisterUseCase;
import com.smartwallet.application.port.out.IbanEncryptionPort;
import com.smartwallet.application.port.out.PasswordHasherPort;
import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.domain.exception.EmailAlreadyExistsException;
import com.smartwallet.domain.model.EncryptedIban;
import com.smartwallet.domain.model.User;

import java.util.UUID;


public class RegisterService{

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final IbanEncryptionPort ibanEncryptionPort;

    public RegisterService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            IbanEncryptionPort ibanEncryptionPort) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.ibanEncryptionPort = ibanEncryptionPort;
    }


    public User register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());
        User user = User.register(UUID.randomUUID(), command.email(), passwordHash);

        if (command.iban() != null && !command.iban().isBlank()) {
            EncryptedIban encryptedIban = ibanEncryptionPort.encrypt(command.iban());
            user = user.withEncryptedIban(encryptedIban);
        }

        return userRepository.save(user);
    }
}
