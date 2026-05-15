package br.com.miriageekstore.identity.domain.port.in;

public interface ResendVerificationUseCase {
    void execute(String email);
}
