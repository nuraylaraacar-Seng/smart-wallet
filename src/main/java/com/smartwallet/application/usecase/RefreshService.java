package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.RefreshCommand;
import com.smartwallet.application.port.in.RefreshResult;
import com.smartwallet.application.port.in.RefreshUseCase;
import com.smartwallet.application.port.out.RefreshTokenPort;
import com.smartwallet.application.port.out.TokenGeneratorPort;
import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.domain.exception.InvalidRefreshTokenException;
import com.smartwallet.domain.model.User;

import java.util.UUID;

/**
 * Refresh token rotation: gelen refresh token doğrulanır, HEMEN blacklist'e
 * eklenir (tekrar kullanılamasın diye), ve hem yeni bir access token hem
 * yeni bir refresh token üretilir. Bu, "refresh token rotation" olarak
 * bilinen standart pratik -- bir refresh token çalınıp kullanılırsa,
 * gerçek kullanıcı bir sonraki normal refresh'inde eski token'ın zaten
 * blacklist'te olduğunu görüp durumu fark edebilir.
 */
public class RefreshService implements RefreshUseCase {

    private final RefreshTokenPort refreshTokenPort;
    private final TokenGeneratorPort tokenGenerator;
    private final UserRepositoryPort userRepository;

    public RefreshService(
            RefreshTokenPort refreshTokenPort,
            TokenGeneratorPort tokenGenerator,
            UserRepositoryPort userRepository) {
        this.refreshTokenPort = refreshTokenPort;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshResult refresh(RefreshCommand command) {
        UUID userId = refreshTokenPort.validateAndExtractUserId(command.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        // Rotation: eski token'ı hemen geçersiz kıl.
        refreshTokenPort.blacklist(command.refreshToken());

        String newAccessToken = tokenGenerator.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = tokenGenerator.generateRefreshToken(user.getId());

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}