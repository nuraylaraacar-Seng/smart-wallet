package com.smartwallet.application.port.out;

import com.smartwallet.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepositoryPort {

    Optional<Wallet> findById(UUID id);

    Optional<Wallet> findByIdWithLock(UUID id);

    Wallet save(Wallet wallet);
}
