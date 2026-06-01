package br.com.miriageekstore.identity.domain.port.in;

public interface LogoutUseCase {
    void execute(String rawRefreshToken);
}
