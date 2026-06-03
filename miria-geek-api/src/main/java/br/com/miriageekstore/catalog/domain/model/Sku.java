package br.com.miriageekstore.catalog.domain.model;

import java.text.Normalizer;
import java.util.Locale;

public record Sku(String value) {

    public Sku {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SKU não pode ser vazio");
        }
    }

    public static Sku generate(Slug productSlug, String attributeName, String attributeValue) {
        String raw = productSlug.value() + "-" + attributeName + "-" + attributeValue;
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        String sku = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .strip()
                .toUpperCase(Locale.ROOT);
        return new Sku(sku);
    }
}
