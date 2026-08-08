package com.smartwallet.application.port.out;

import com.smartwallet.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    User save(User user);

    boolean existsByEmail(String email);
}
