package br.com.miriageekstore.identity.domain.port.in;

public interface PatchUserUseCase {
    UpdateUserResult execute(PatchUserCommand command);
}
