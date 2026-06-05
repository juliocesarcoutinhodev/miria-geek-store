package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListVariantsResult(List<VariantItem> variants) {

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
