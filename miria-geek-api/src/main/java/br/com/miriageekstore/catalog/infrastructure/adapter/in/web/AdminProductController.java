package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.CreateProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Products", description = "Gestão de produtos — requer ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final CreateProductUseCase createProductUseCase;
    private final ProductWebMapper mapper;

    @Operation(summary = "Cadastrar novo produto com variantes",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return mapper.toResponse(createProductUseCase.execute(mapper.toCommand(request)));
    }
}
