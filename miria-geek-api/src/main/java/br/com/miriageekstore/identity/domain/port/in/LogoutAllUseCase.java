package br.com.miriageekstore.identity.domain.port.in;

public interface LogoutAllUseCase {
    void execute(String rawRefreshToken);
}
