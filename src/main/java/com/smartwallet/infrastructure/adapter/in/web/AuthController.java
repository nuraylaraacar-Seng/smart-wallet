package com.smartwallet.infrastructure.adapter.in.web;

import com.smartwallet.application.port.in.LoginCommand;
import com.smartwallet.application.port.in.LoginResult;
import com.smartwallet.application.port.in.LoginUseCase;
import com.smartwallet.application.port.in.LogoutCommand;
import com.smartwallet.application.port.in.LogoutUseCase;
import com.smartwallet.application.port.in.RefreshCommand;
import com.smartwallet.application.port.in.RefreshResult;
import com.smartwallet.application.port.in.RefreshUseCase;
import com.smartwallet.application.port.in.RegisterCommand;
import com.smartwallet.application.port.in.RegisterUseCase;
import com.smartwallet.domain.exception.InvalidRefreshTokenException;
import com.smartwallet.domain.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final int REFRESH_COOKIE_MAX_AGE_SECONDS = 5 * 24 * 60 * 60;

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(
            RegisterUseCase registerUseCase,
            LoginUseCase loginUseCase,
            RefreshUseCase refreshUseCase,
            LogoutUseCase logoutUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshUseCase = refreshUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterApiRequest request) {
        RegisterCommand command = new RegisterCommand(request.email(), request.password(), request.iban());
        User user = registerUseCase.register(command);
        return ResponseEntity.ok(new RegisterResponse(user.getId().toString(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginApiRequest request,
            HttpServletResponse response) {

        LoginCommand command = new LoginCommand(request.email(), request.password());
        LoginResult result = loginUseCase.login(command);

        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = readRefreshTokenCookie(request)
                .orElseThrow(InvalidRefreshTokenException::new);

        RefreshResult result = refreshUseCase.refresh(new RefreshCommand(refreshToken));

        // Rotation: cookie'deki eski token, yeni üretilen tokenla değiştirilir.
        setRefreshTokenCookie(response, result.newRefreshToken());
        return ResponseEntity.ok(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        readRefreshTokenCookie(request)
                .ifPresent(token -> logoutUseCase.logout(new LogoutCommand(token)));

        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        // NOT: localhost'ta HTTP üzerinden test ederken bu satırı geçici
        // olarak false yapman gerekebilir. Oracle Cloud'a HTTPS ile
        // deploy ettiğinde true'ya geri döndür -- Secure olmayan cookie
        // prod'da asla kabul edilmemeli.
        cookie.setSecure(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(REFRESH_COOKIE_MAX_AGE_SECONDS);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private java.util.Optional<String> readRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return java.util.Optional.empty();
        }
        return java.util.Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
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

    public record AccessTokenResponse(String accessToken) {
    }

    public record RegisterResponse(String userId, String email) {
    }
}