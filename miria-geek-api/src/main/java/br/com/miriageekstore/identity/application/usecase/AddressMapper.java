package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.port.in.AddressResult;

final class AddressMapper {

    private AddressMapper() {}

    static AddressResult toResult(Address a) {
        return new AddressResult(
                a.getId(), a.getAlias(), a.getZipCode(), a.getStreet(), a.getNumber(),
                a.getComplement(), a.getNeighborhood(), a.getCity(), a.getState(),
                a.isDefault(), a.getCreatedAt()
        );
    }
}
