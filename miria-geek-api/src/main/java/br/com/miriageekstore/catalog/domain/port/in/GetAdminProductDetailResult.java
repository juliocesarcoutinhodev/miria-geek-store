package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetAdminProductDetailResult(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        String categoryName,
        String status,
        boolean featured,
        Instant createdAt,
        List<ImageItem> images,
        List<VariantItem> variants
) {
    public record ImageItem(
            UUID id,
            String url,
            boolean principal,
            int imageOrder
    ) {}

    public record VariantItem(
            UUID id,
            String attributeName,
            String attributeValue,
            BigDecimal price,
            int stock,
            String sku,
            boolean active,
            Instant createdAt,
            BigDecimal weight,
            BigDecimal width,
            BigDecimal height,
            BigDecimal depth
    ) {}
}
