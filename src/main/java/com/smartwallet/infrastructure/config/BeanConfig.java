package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.out.AuditLogPort;
import com.smartwallet.application.port.out.IbanEncryptionPort;
import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.LoginAttemptPort;
import com.smartwallet.application.port.out.PasswordHasherPort;
import com.smartwallet.application.port.out.TokenGeneratorPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.application.usecase.DepositService;
import com.smartwallet.application.usecase.LoginService;
import com.smartwallet.application.usecase.RegisterService;
import com.smartwallet.application.usecase.TransferMoneyService;
import com.smartwallet.application.usecase.WithdrawService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class BeanConfig {

    @Bean
    public TransferMoneyService transferMoneyService(
            WalletRepositoryPort walletRepository,
            TransactionRepositoryPort transactionRepository,
            IdempotencyKeyPort idempotencyKeyPort,
            AuditLogPort auditLogPort,
            ApplicationEventPublisher eventPublisher) {
        return new TransferMoneyService(
                walletRepository, transactionRepository, idempotencyKeyPort, auditLogPort, eventPublisher);
    }

    @Bean
    public WithdrawService withdrawService(
            WalletRepositoryPort walletRepository,
            TransactionRepositoryPort transactionRepository,
            IdempotencyKeyPort idempotencyKeyPort,
            AuditLogPort auditLogPort) {
        return new WithdrawService(walletRepository, transactionRepository, idempotencyKeyPort, auditLogPort);
    }

    @Bean
    public DepositService depositService(
            WalletRepositoryPort walletRepository,
            TransactionRepositoryPort transactionRepository,
            IdempotencyKeyPort idempotencyKeyPort,
            AuditLogPort auditLogPort) {
        return new DepositService(walletRepository, transactionRepository, idempotencyKeyPort, auditLogPort);
    }

    @Bean
    public RegisterService registerService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            IbanEncryptionPort ibanEncryptionPort) {
        return new RegisterService(userRepository, passwordHasher, ibanEncryptionPort);
    }

    @Bean
    public LoginService loginService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            TokenGeneratorPort tokenGenerator,
            LoginAttemptPort loginAttemptPort) {
        return new LoginService(userRepository, passwordHasher, tokenGenerator, loginAttemptPort);
    }
}