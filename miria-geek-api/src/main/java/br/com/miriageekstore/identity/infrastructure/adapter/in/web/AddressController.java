package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.AddressCommand;
import br.com.miriageekstore.identity.domain.port.in.AddressResult;
import br.com.miriageekstore.identity.domain.port.in.CreateAddressUseCase;
import br.com.miriageekstore.identity.domain.port.in.DeleteAddressUseCase;
import br.com.miriageekstore.identity.domain.port.in.ListAddressesUseCase;
import br.com.miriageekstore.identity.domain.port.in.SetDefaultAddressUseCase;
import br.com.miriageekstore.identity.domain.port.in.UpdateAddressUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Addresses", description = "Gestão de endereços de entrega")
@RestController
@RequestMapping("/api/v1/users/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final ListAddressesUseCase listAddressesUseCase;
    private final CreateAddressUseCase createAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;
    private final SetDefaultAddressUseCase setDefaultAddressUseCase;

    @Operation(summary = "Listar endereços do usuário", security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping
    List<AddressResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return listAddressesUseCase.execute(userId(jwt)).stream()
                .map(this::toResponse)
                .toList();
    }

    @Operation(summary = "Cadastrar novo endereço", security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AddressResponse create(@AuthenticationPrincipal Jwt jwt,
                           @Valid @RequestBody AddressRequest request) {
        return toResponse(createAddressUseCase.execute(userId(jwt), toCommand(request)));
    }

    @Operation(summary = "Atualizar endereço", security = @SecurityRequirement(name = "cookieAuth"))
    @PutMapping("/{id}")
    AddressResponse update(@AuthenticationPrincipal Jwt jwt,
                           @PathVariable UUID id,
                           @Valid @RequestBody AddressRequest request) {
        return toResponse(updateAddressUseCase.execute(userId(jwt), id, toCommand(request)));
    }

    @Operation(summary = "Remover endereço", security = @SecurityRequirement(name = "cookieAuth"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        deleteAddressUseCase.execute(userId(jwt), id);
    }

    @Operation(summary = "Definir endereço como padrão", security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}/default")
    AddressResponse setDefault(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return toResponse(setDefaultAddressUseCase.execute(userId(jwt), id));
    }

    private UserId userId(Jwt jwt) {
        return UserId.of(UUID.fromString(jwt.getSubject()));
    }

    private AddressCommand toCommand(AddressRequest r) {
        return new AddressCommand(r.alias(), r.zipCode(), r.street(), r.number(),
                r.complement(), r.neighborhood(), r.city(), r.state(), r.isDefault());
    }

    private AddressResponse toResponse(AddressResult r) {
        return new AddressResponse(r.id(), r.alias(), r.zipCode(), r.street(), r.number(),
                r.complement(), r.neighborhood(), r.city(), r.state(), r.isDefault(), r.createdAt());
    }
}
