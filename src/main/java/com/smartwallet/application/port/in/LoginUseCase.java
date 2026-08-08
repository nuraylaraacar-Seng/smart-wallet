package com.smartwallet.application.port.in;

public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}
