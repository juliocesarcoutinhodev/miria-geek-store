package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

record ReorderImagesRequest(
        @NotNull
        @Size(min = 1, message = "A lista de ordenação não pode ser vazia")
        @Valid
        List<ImageOrderItem> items
) {
    record ImageOrderItem(
            @NotNull(message = "O ID da imagem é obrigatório")
            UUID imageId,

            @Min(value = 0, message = "A ordem não pode ser negativa")
            int order
    ) {}
}
