package com.smartwallet.infrastructure.adapter.in.web;

import com.smartwallet.application.port.in.LoginCommand;
import com.smartwallet.application.port.in.LoginResult;
import com.smartwallet.application.port.in.LoginUseCase;
import com.smartwallet.application.port.in.RegisterCommand;
import com.smartwallet.application.port.in.RegisterUseCase;
import com.smartwallet.domain.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterApiRequest request) {
        RegisterCommand command = new RegisterCommand(request.email(), request.password(), request.iban());
        User user = registerUseCase.register(command);
        return ResponseEntity.ok(new RegisterResponse(user.getId().toString(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@Valid @RequestBody LoginApiRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        LoginResult result = loginUseCase.login(command);
        return ResponseEntity.ok(result);
    }

    public record RegisterApiRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password,
            String iban) {
    }

    public record LoginApiRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record RegisterResponse(String userId, String email) {
    }
}
