package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

public interface GetUserByIdUseCase {
    GetUserByIdResult execute(UserId id);
}
