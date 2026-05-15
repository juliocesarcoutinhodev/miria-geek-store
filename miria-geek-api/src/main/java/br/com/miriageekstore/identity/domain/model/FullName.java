package br.com.miriageekstore.identity.domain.model;

public record FullName(String value) {

    public FullName {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Full name cannot be blank");
        value = value.strip();
        if (value.length() < 3 || value.length() > 255) {
            throw new IllegalArgumentException("Full name must be between 3 and 255 characters");
        }
    }

    public static FullName of(String value) {
        return new FullName(value);
    }
}
