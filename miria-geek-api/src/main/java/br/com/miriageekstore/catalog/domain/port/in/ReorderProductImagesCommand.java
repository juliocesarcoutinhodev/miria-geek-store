package br.com.miriageekstore.catalog.domain.port.in;

import java.util.List;
import java.util.UUID;

public record ReorderProductImagesCommand(
        UUID productId,
        List<ImageOrderItem> items
) {
    public record ImageOrderItem(UUID imageId, int order) {}
}
