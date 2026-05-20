package br.com.miriageekstore.identity.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Token inválido ou expirado");
    }
}
