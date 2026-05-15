package br.com.miriageekstore.identity.domain.model;

import br.com.miriageekstore.identity.domain.exception.InvalidEmailException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank()) throw new InvalidEmailException("Email cannot be blank");
        value = value.strip().toLowerCase();
        if (!PATTERN.matcher(value).matches()) throw new InvalidEmailException(value);
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
