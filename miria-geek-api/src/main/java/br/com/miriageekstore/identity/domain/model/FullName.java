package br.com.miriageekstore.identity.domain.model;

public record FullName(String value) {

    public FullName {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("O nome completo não pode estar em branco");
        value = value.strip();
        if (value.length() < 3 || value.length() > 255) {
            throw new IllegalArgumentException("O nome completo deve conter entre 3 e 255 caracteres");
        }
    }

    public static FullName of(String value) {
        return new FullName(value);
    }
}
