package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

import java.util.List;

public interface ListAddressesUseCase {
    List<AddressResult> execute(UserId userId);
}
