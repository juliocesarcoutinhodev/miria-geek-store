package br.com.miriageekstore.identity.domain.port.in;

public interface RefreshTokenUseCase {
    LoginResult execute(String rawToken, String ipAddress, String userAgent);
}
