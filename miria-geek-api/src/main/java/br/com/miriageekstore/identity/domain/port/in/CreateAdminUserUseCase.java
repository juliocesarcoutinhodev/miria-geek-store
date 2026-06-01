package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

public interface CreateAdminUserUseCase {
    CreateAdminUserResult execute(UserId createdByAdminId, CreateAdminUserCommand command);
}
