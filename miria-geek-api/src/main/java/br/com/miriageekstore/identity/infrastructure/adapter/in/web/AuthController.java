package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.port.in.ForgotPasswordCommand;
import br.com.miriageekstore.identity.domain.port.in.ForgotPasswordUseCase;
import br.com.miriageekstore.identity.domain.port.in.LoginUseCase;
import br.com.miriageekstore.identity.domain.port.in.LogoutAllUseCase;
import br.com.miriageekstore.identity.domain.port.in.LogoutUseCase;
import br.com.miriageekstore.identity.domain.port.in.RefreshTokenUseCase;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserUseCase;
import br.com.miriageekstore.identity.domain.port.in.ResendVerificationUseCase;
import br.com.miriageekstore.identity.domain.port.in.ResetPasswordCommand;
import br.com.miriageekstore.identity.domain.port.in.ResetPasswordUseCase;
import br.com.miriageekstore.identity.domain.port.in.VerifyEmailUseCase;
import br.com.miriageekstore.identity.infrastructure.config.CookieFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final LogoutAllUseCase logoutAllUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final CookieFactory cookieFactory;
    private final AuthMapper authMapper;

    @Operation(summary = "Cadastrar novo cliente")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        var result = registerUserUseCase.execute(authMapper.toCommand(request));
        return authMapper.toResponse(result);
    }

    @Operation(summary = "Verificar e-mail do cliente")
    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    VerifyEmailResponse verifyEmail(@RequestParam UUID token) {
        verifyEmailUseCase.execute(token);
        return new VerifyEmailResponse("E-mail verificado com sucesso. Sua conta está ativa.");
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
        var result = loginUseCase.execute(authMapper.toCommand(
                request,
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.accessToken(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.refreshToken(result.refreshToken()).toString())
                .body(authMapper.toResponse(result));
    }

    @Operation(summary = "Renovar tokens de acesso")
    @PostMapping("/refresh")
    ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String rawRefreshToken,
            HttpServletRequest httpRequest) {
        var result = refreshTokenUseCase.execute(
                rawRefreshToken,
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.accessToken(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.refreshToken(result.refreshToken()).toString())
                .body(authMapper.toResponse(result));
    }

    @Operation(summary = "Encerrar sessão atual")
    @PostMapping("/logout")
    ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String rawRefreshToken) {
        logoutUseCase.execute(rawRefreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearRefreshToken().toString())
                .build();
    }

    @Operation(summary = "Encerrar sessão em todos os dispositivos")
    @PostMapping("/logout-all")
    ResponseEntity<Void> logoutAll(
            @CookieValue(name = "refresh_token", required = false) String rawRefreshToken) {
        logoutAllUseCase.execute(rawRefreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearRefreshToken().toString())
                .build();
    }

    @Operation(summary = "Solicitar redefinição de senha")
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.execute(new ForgotPasswordCommand(request.email()));
    }

    @Operation(summary = "Redefinir senha com token")
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(new ResetPasswordCommand(
                request.token(), request.newPassword(), request.passwordConfirmation()));
    }
}
