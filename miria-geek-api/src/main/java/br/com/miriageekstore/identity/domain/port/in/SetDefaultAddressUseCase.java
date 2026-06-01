package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

import java.util.UUID;

public interface SetDefaultAddressUseCase {
    AddressResult execute(UserId userId, UUID addressId);
}
