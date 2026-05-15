package br.com.miriageekstore.identity.domain.port.in;

public interface LoginUseCase {
    LoginResult execute(LoginCommand command);
}
