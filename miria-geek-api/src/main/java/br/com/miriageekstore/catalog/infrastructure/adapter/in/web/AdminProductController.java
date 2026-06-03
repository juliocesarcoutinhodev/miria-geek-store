package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.exception.StorageException;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductUseCase;
import br.com.miriageekstore.catalog.domain.port.in.DeleteProductImageCommand;
import br.com.miriageekstore.catalog.domain.port.in.DeleteProductImageUseCase;
import br.com.miriageekstore.catalog.domain.port.in.ReorderProductImagesUseCase;
import br.com.miriageekstore.catalog.domain.port.in.SetPrincipalImageCommand;
import br.com.miriageekstore.catalog.domain.port.in.SetPrincipalImageUseCase;
import br.com.miriageekstore.catalog.domain.port.in.UploadProductImageCommand;
import br.com.miriageekstore.catalog.domain.port.in.UploadProductImageUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Admin Products", description = "Gestão de produtos — requer ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final SetPrincipalImageUseCase setPrincipalImageUseCase;
    private final ReorderProductImagesUseCase reorderProductImagesUseCase;
    private final DeleteProductImageUseCase deleteProductImageUseCase;
    private final ProductWebMapper mapper;

    @Operation(summary = "Cadastrar novo produto com variantes",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return mapper.toResponse(createProductUseCase.execute(mapper.toCommand(request)));
    }

    @Operation(summary = "Fazer upload de imagem do produto",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ProductImageResponse uploadImage(@PathVariable UUID id,
                                     @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            var command = new UploadProductImageCommand(
                    id, arquivo.getInputStream(),
                    arquivo.getContentType(), arquivo.getSize());
            return mapper.toImageResponse(uploadProductImageUseCase.execute(command));
        } catch (IOException e) {
            throw new StorageException("Erro ao processar o arquivo enviado", e);
        }
    }

    @Operation(summary = "Definir imagem principal do produto",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}/images/{imageId}/principal")
    ProductImageResponse setPrincipalImage(@PathVariable UUID id, @PathVariable UUID imageId) {
        return mapper.toImageResponse(
                setPrincipalImageUseCase.execute(new SetPrincipalImageCommand(id, imageId)));
    }

    @Operation(summary = "Reordenar imagens do produto",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}/images/order")
    List<ProductImageResponse> reorderImages(@PathVariable UUID id,
                                              @Valid @RequestBody ReorderImagesRequest request) {
        return mapper.toImageResponseList(
                reorderProductImagesUseCase.execute(mapper.toReorderCommand(id, request)));
    }

    @Operation(summary = "Remover imagem do produto",
               security = @SecurityRequirement(name = "cookieAuth"))
    @DeleteMapping("/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteImage(@PathVariable UUID id, @PathVariable UUID imageId) {
        deleteProductImageUseCase.execute(new DeleteProductImageCommand(id, imageId));
    }
}
