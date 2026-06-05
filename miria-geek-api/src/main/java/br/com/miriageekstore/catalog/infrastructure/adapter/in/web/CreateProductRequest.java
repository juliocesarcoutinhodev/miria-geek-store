package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

record CreateProductRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId,

        Boolean featured,

        @NotNull(message = "As variantes são obrigatórias")
        @Size(min = 1, message = "O produto deve ter ao menos uma variante")
        @Valid
        List<VariantRequest> variants
) {
    record VariantRequest(
            @NotBlank(message = "O nome do atributo é obrigatório")
            String attributeName,

            @NotBlank(message = "O valor do atributo é obrigatório")
            String attributeValue,

            @NotNull(message = "O preço é obrigatório")
            @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
            BigDecimal price,

            @Min(value = 0, message = "O estoque não pode ser negativo")
            int stock,

            String sku,

            @NotNull @Positive BigDecimal weight,
            @NotNull @Positive BigDecimal width,
            @NotNull @Positive BigDecimal height,
            @NotNull @Positive BigDecimal depth
    ) {}
}
