package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductCommand(
        String name,
        String description,
        UUID categoryId,
        boolean featured,
        List<VariantInput> variants
) {
    public record VariantInput(
            String attributeName,
            String attributeValue,
            BigDecimal price,
            int stock,
            String sku
    ) {}
}
