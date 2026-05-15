package br.com.miriageekstore.identity.domain.port.in;

public interface RegisterUserUseCase {
    RegisterUserResult execute(RegisterUserCommand command);
}
