package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.model.Wallet;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final SpringDataWalletRepository springDataWalletRepository;
    private final WalletMapper walletMapper;
    private final EntityManager entityManager;

    public WalletRepositoryAdapter(
            SpringDataWalletRepository springDataWalletRepository,
            WalletMapper walletMapper,
            EntityManager entityManager) {
        this.springDataWalletRepository = springDataWalletRepository;
        this.walletMapper = walletMapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Wallet save(Wallet wallet) {
        WalletEntity existingEntity = entityManager.find(WalletEntity.class, wallet.getId());

        if (existingEntity == null) {
            // DÜZELTME: Yeni cüzdanda creation ve version null olmalı ki Hibernate yeni kayıt (INSERT) olduğunu bilsin!
            WalletEntity entity = walletMapper.toEntity(wallet, null);
            entityManager.persist(entity);
            return walletMapper.toDomain(entity);
        } else {
            existingEntity.setBalanceAmount(wallet.getBalance().getAmount());
            existingEntity.setBalanceCurrency(wallet.getBalance().getCurrency().getCurrencyCode());
            existingEntity.setStatus(wallet.getStatus().name());
            existingEntity.setUpdatedAt(Instant.now());
            return walletMapper.toDomain(existingEntity);
        }
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
    public Optional<Wallet> findByUserId(UUID userId) {
        return springDataWalletRepository.findByUserId(userId).map(walletMapper::toDomain);
    }
}