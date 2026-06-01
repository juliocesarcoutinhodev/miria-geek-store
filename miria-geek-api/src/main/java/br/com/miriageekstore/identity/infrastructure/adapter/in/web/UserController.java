package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.ChangePasswordCommand;
import br.com.miriageekstore.identity.domain.port.in.ChangePasswordUseCase;
import br.com.miriageekstore.identity.domain.port.in.GetProfileUseCase;
import br.com.miriageekstore.identity.domain.port.in.ProfileResult;
import br.com.miriageekstore.identity.domain.port.in.UpdateProfileCommand;
import br.com.miriageekstore.identity.domain.port.in.UpdateProfileUseCase;
import br.com.miriageekstore.identity.infrastructure.config.CookieFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Users", description = "Endpoints de usuário autenticado")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final CookieFactory cookieFactory;

    @Operation(summary = "Consultar perfil do usuário autenticado", security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping("/me")
    ProfileResponse getProfile(@AuthenticationPrincipal Jwt jwt) {
        var userId = UserId.of(UUID.fromString(jwt.getSubject()));
        return toResponse(getProfileUseCase.execute(userId));
    }

    @Operation(summary = "Atualizar perfil do usuário autenticado", security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/me")
    ProfileResponse updateProfile(@AuthenticationPrincipal Jwt jwt,
                                  @Valid @RequestBody UpdateProfileRequest request) {
        var userId = UserId.of(UUID.fromString(jwt.getSubject()));
        return toResponse(updateProfileUseCase.execute(userId, new UpdateProfileCommand(request.fullName())));
    }

    @Operation(summary = "Alterar senha do usuário autenticado", security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/me/password")
    ResponseEntity<Void> changePassword(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody ChangePasswordRequest request,
                                        HttpServletRequest httpRequest) {
        var userId = UserId.of(UUID.fromString(jwt.getSubject()));
        var result = changePasswordUseCase.execute(userId, new ChangePasswordCommand(
                request.currentPassword(),
                request.newPassword(),
                request.passwordConfirmation(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        ));
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.accessToken(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.refreshToken(result.refreshToken()).toString())
                .build();
    }

    private ProfileResponse toResponse(ProfileResult result) {
        return new ProfileResponse(
                result.id(), result.fullName(), result.email(),
                result.status(), result.roles(), result.createdAt()
        );
    }
}
