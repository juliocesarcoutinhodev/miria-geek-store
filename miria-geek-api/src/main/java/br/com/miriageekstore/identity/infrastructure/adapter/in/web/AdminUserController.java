package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserCommand;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Admin", description = "Endpoints de administração — requerem ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final CreateAdminUserUseCase createAdminUserUseCase;

    @Operation(summary = "Criar novo usuário administrador",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AdminUserResponse createAdmin(@AuthenticationPrincipal Jwt jwt,
                                   @Valid @RequestBody CreateAdminUserRequest request) {
        var creatorId = UserId.of(UUID.fromString(jwt.getSubject()));
        var result = createAdminUserUseCase.execute(creatorId,
                new CreateAdminUserCommand(request.fullName(), request.email()));
        return new AdminUserResponse(
                result.id(), result.fullName(), result.email(),
                result.status(), result.role(),
                result.createdAt(), result.createdByAdminId()
        );
    }
}
