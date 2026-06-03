package br.com.miriageekstore.catalog.domain.model;

import java.text.Normalizer;
import java.util.Locale;

public record Slug(String value) {

    public static Slug from(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .strip();
        return new Slug(slug);
    }
}
