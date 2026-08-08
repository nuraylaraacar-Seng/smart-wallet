package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.application.port.out.UserRepositoryPort;
import com.smartwallet.domain.model.EncryptedIban;
import com.smartwallet.domain.model.User;
import com.smartwallet.domain.model.UserStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    public UserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        Instant existingCreatedAt = repository.findById(user.getId())
                .map(UserEntity::getCreatedAt)
                .orElse(null);

        UserEntity entity = toEntity(user, existingCreatedAt);
        UserEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private User toDomain(UserEntity entity) {
        EncryptedIban encryptedIban = null;
        if (entity.getEncryptedIbanData() != null) {
            encryptedIban = new EncryptedIban(
                    entity.getEncryptedIbanData(),
                    entity.getEncryptedIbanKey(),
                    entity.getIbanIv());
        }

        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                encryptedIban,
                UserStatus.valueOf(entity.getStatus()));
    }

    private UserEntity toEntity(User user, Instant existingCreatedAt) {
        EncryptedIban iban = user.getEncryptedIban();
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                iban != null ? iban.getEncryptedData() : null,
                iban != null ? iban.getEncryptedDataKey() : null,
                iban != null ? iban.getIv() : null,
                user.getStatus().name(),
                existingCreatedAt != null ? existingCreatedAt : Instant.now());
    }
}

