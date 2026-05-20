package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.port.in.LoginCommand;
import br.com.miriageekstore.identity.domain.port.in.LoginUseCase;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserCommand;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserUseCase;
import br.com.miriageekstore.identity.domain.port.in.ResendVerificationUseCase;
import br.com.miriageekstore.identity.domain.port.in.VerifyEmailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Authentication", description = "Endpoints de autenticação e cadastro")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;
    private final LoginUseCase loginUseCase;

    @Value("${app.cookie.domain:localhost}")
    private String cookieDomain;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.jwt.refresh-token-expiry-seconds:2592000}")
    private long refreshTokenExpirySeconds;

    @Operation(summary = "Cadastrar novo cliente")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        var result = registerUserUseCase.execute(new RegisterUserCommand(
                request.fullName(),
                request.email(),
                request.password(),
                request.passwordConfirmation()
        ));
        return RegisterResponse.from(result);
    }

    @Operation(summary = "Verificar e-mail do cliente")
    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    VerifyEmailResponse verifyEmail(@RequestParam UUID token) {
        verifyEmailUseCase.execute(token);
        return new VerifyEmailResponse("Email verified successfully. Your account is now active.");
    }

    @Operation(summary = "Reenviar e-mail de verificação")
    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        resendVerificationUseCase.execute(request.email());
    }

    @Operation(summary = "Login do cliente")
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                        HttpServletRequest httpRequest) {
        var result = loginUseCase.execute(new LoginCommand(
                request.email(),
                request.password(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        ));

        var accessCookie = ResponseCookie.from("access_token", result.accessToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .domain(cookieDomain)
                .sameSite("Strict")
                .maxAge(900)
                .build();

        var refreshCookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/v1/auth/refresh")
                .domain(cookieDomain)
                .sameSite("Strict")
                .maxAge(refreshTokenExpirySeconds)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(result.id(), result.name(), result.email(), result.roles(), result.status()));
    }
}
