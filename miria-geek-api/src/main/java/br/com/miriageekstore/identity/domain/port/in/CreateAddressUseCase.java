package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

public interface CreateAddressUseCase {
    AddressResult execute(UserId userId, AddressCommand command);
}
