package br.com.miriageekstore.identity.domain.port.out;

public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hash);
}
