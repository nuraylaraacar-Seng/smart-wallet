package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final SpringDataWalletRepository springDataWalletRepository;
    private final WalletMapper walletMapper;

    public WalletRepositoryAdapter(SpringDataWalletRepository springDataWalletRepository, WalletMapper walletMapper) {
        this.springDataWalletRepository = springDataWalletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return springDataWalletRepository.findById(id).map(walletMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByIdWithLock(UUID id) {
        return springDataWalletRepository.findByIdWithLock(id).map(walletMapper::toDomain);
    }

    @Override
    public Wallet save(Wallet wallet) {

        Instant existingCreatedAt = springDataWalletRepository.findById(wallet.getId())
                .map(WalletEntity::getCreatedAt)
                .orElse(null);

        WalletEntity entity = walletMapper.toEntity(wallet, existingCreatedAt);
        WalletEntity saved = springDataWalletRepository.save(entity);
        return walletMapper.toDomain(saved);
    }
}
