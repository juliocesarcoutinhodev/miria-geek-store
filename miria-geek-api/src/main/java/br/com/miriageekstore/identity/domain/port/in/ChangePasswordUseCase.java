package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

public interface ChangePasswordUseCase {
    ChangePasswordResult execute(UserId userId, ChangePasswordCommand command);
}
