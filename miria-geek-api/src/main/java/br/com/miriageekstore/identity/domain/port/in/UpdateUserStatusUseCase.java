package br.com.miriageekstore.identity.domain.port.in;

public interface UpdateUserStatusUseCase {
    UpdateUserStatusResult execute(UpdateUserStatusCommand command);
}
