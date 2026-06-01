package br.com.miriageekstore.identity.domain.port.in;

public interface UpdateUserUseCase {
    UpdateUserResult execute(UpdateUserCommand command);
}
