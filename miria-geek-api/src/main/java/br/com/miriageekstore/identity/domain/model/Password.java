package br.com.miriageekstore.identity.domain.model;

import br.com.miriageekstore.identity.domain.exception.InvalidPasswordPolicyException;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;

import java.util.regex.Pattern;

public record Password(String hash) {

    private static final int MIN_LENGTH = 8;
    private static final Pattern POLICY = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).+$");

    public Password {
        if (hash == null || hash.isBlank()) throw new IllegalArgumentException("Password hash cannot be blank");
    }

    public static Password hash(String raw, PasswordHasher hasher) {
        validatePolicy(raw);
        return new Password(hasher.hash(raw));
    }

    public static Password fromHash(String hash) {
        return new Password(hash);
    }

    private static void validatePolicy(String raw) {
        if (raw == null || raw.length() < MIN_LENGTH) {
            throw new InvalidPasswordPolicyException(
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }
        if (!POLICY.matcher(raw).matches()) {
            throw new InvalidPasswordPolicyException(
                    "Password must contain at least one letter and one number");
        }
    }
}
