package com.smartwallet.application.port.in;

import com.smartwallet.domain.model.User;

public interface RegisterUseCase {
    User register(RegisterCommand command);
}
