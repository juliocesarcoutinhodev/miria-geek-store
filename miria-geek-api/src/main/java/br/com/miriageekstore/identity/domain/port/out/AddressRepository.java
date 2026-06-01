package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository {
    List<Address> findAllByUserId(UserId userId);
    Optional<Address> findByIdAndUserId(UUID id, UserId userId);
    Address save(Address address);
    void delete(UUID id);
    int countByUserId(UserId userId);
    void clearDefaultByUserId(UserId userId);
}
