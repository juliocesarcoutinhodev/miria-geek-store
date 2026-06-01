package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserCommand;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserUseCase;
import br.com.miriageekstore.identity.domain.port.in.GetUserByIdUseCase;
import br.com.miriageekstore.identity.domain.port.in.ListUsersQuery;
import br.com.miriageekstore.identity.domain.port.in.ListUsersUseCase;
import br.com.miriageekstore.identity.domain.port.in.PatchUserCommand;
import br.com.miriageekstore.identity.domain.port.in.PatchUserUseCase;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserCommand;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserStatusCommand;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserStatusUseCase;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Admin", description = "Endpoints de administração — requerem ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final CreateAdminUserUseCase createAdminUserUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final PatchUserUseCase patchUserUseCase;

    @Operation(summary = "Listar usuários paginado com filtros",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping
    PagedResponse<UserSummaryResponse> listUsers(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        var result = listUsersUseCase.execute(
                new ListUsersQuery(nome, email, status, role, page, size, sort, direction));

        var content = result.content().stream()
                .map(u -> new UserSummaryResponse(
                        u.id(), u.fullName(), u.email(), u.role(), u.status(), u.createdAt()))
                .collect(Collectors.toList());

        return new PagedResponse<>(content, result.page(), result.size(),
                result.totalElements(), result.totalPages());
    }

    @Operation(summary = "Buscar usuário por ID",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping("/{id}")
    AdminUserDetailResponse getUserById(@PathVariable UUID id) {
        var result = getUserByIdUseCase.execute(UserId.of(id));
        return new AdminUserDetailResponse(
                result.id(), result.fullName(), result.email(),
                result.role(), result.status(), result.createdAt(),
                result.totalPedidos(), result.ultimoLogin()
        );
    }

    @Operation(summary = "Criar novo usuário (admin ou customer)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AdminUserResponse createUser(@AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody CreateAdminUserRequest request) {
        var creatorId = UserId.of(UUID.fromString(jwt.getSubject()));
        var result = createAdminUserUseCase.execute(creatorId,
                new CreateAdminUserCommand(request.fullName(), request.email(), request.role()));
        return new AdminUserResponse(
                result.id(), result.fullName(), result.email(),
                result.status(), result.role(),
                result.createdAt(), result.createdByAdminId()
        );
    }

    @Operation(summary = "Atualização completa de usuário (PUT)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PutMapping("/{id}")
    AdminUserResponse updateUser(@AuthenticationPrincipal Jwt jwt,
                                 @PathVariable UUID id,
                                 @Valid @RequestBody UpdateUserRequest request) {
        var adminId = UserId.of(UUID.fromString(jwt.getSubject()));
        var result = updateUserUseCase.execute(
                new UpdateUserCommand(UserId.of(id), adminId,
                        request.fullName(), request.email(), request.role()));
        return new AdminUserResponse(
                result.id(), result.fullName(), result.email(),
                result.status(), result.role(),
                result.createdAt(), null
        );
    }

    @Operation(summary = "Atualização parcial de usuário (PATCH)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}")
    AdminUserResponse patchUser(@AuthenticationPrincipal Jwt jwt,
                                @PathVariable UUID id,
                                @Valid @RequestBody PatchUserRequest request) {
        var adminId = UserId.of(UUID.fromString(jwt.getSubject()));
        var result = patchUserUseCase.execute(
                new PatchUserCommand(UserId.of(id), adminId,
                        request.fullName(), request.email(), request.role()));
        return new AdminUserResponse(
                result.id(), result.fullName(), result.email(),
                result.status(), result.role(),
                result.createdAt(), null
        );
    }

    @Operation(summary = "Ativar ou desativar conta de usuário",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}/status")
    UserStatusResponse updateStatus(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable UUID id,
                                    @Valid @RequestBody UpdateUserStatusRequest request) {
        var requesterId = UserId.of(UUID.fromString(jwt.getSubject()));
        var result = updateUserStatusUseCase.execute(
                new UpdateUserStatusCommand(UserId.of(id), request.status(), requesterId));
        return new UserStatusResponse(result.id(), result.fullName(), result.email(), result.status());
    }
}
