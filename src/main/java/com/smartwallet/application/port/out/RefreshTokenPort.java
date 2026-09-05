package com.smartwallet.application.port.out;

import java.util.Optional;
import java.util.UUID;


public interface RefreshTokenPort {
    Optional<UUID> validateAndExtractUserId(String refreshToken);

    /**
     * Token'ı blacklist'e ekler (logout  sırasında eskisini
     * geçersiz kılmak için).
     */
    void blacklist(String refreshToken);
}