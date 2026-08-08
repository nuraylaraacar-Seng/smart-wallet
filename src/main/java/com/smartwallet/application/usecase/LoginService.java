package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.LoginCommand;
import com.smartwallet.application.port.in.LoginResult;
import com.smartwallet.application.port.in.LoginUseCase;
import com.smartwallet.application.port.out.LoginAttemptPort;
import com.smartwallet.application.port.out.PasswordHasherPort;
import com.smartwallet.application.port.out.TokenGeneratorPort;
import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.domain.exception.AccountLockedException;
import com.smartwallet.domain.exception.InvalidCredentialsException;
import com.smartwallet.domain.model.User;

public class LoginService{

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenGeneratorPort tokenGenerator;
    private final LoginAttemptPort loginAttemptPort;

    public LoginService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            TokenGeneratorPort tokenGenerator,
            LoginAttemptPort loginAttemptPort) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
        this.loginAttemptPort = loginAttemptPort;
    }


    public LoginResult login(LoginCommand command) {
        if (loginAttemptPort.isLocked(command.email())) {
            throw new AccountLockedException(command.email());
        }

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> {
                    loginAttemptPort.recordFailure(command.email());
                    return new InvalidCredentialsException();
                });

        if (!passwordHasher.matches(command.rawPassword(), user.getPasswordHash())) {
            loginAttemptPort.recordFailure(command.email());
            throw new InvalidCredentialsException();
        }

        loginAttemptPort.recordSuccess(command.email());

        String accessToken = tokenGenerator.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = tokenGenerator.generateRefreshToken(user.getId());

        return new LoginResult(accessToken, refreshToken);
    }
}

