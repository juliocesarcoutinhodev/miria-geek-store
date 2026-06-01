package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class AddressPersistenceAdapter implements AddressRepository {

    private final AddressJpaRepository jpaRepository;

    @Override
    public List<Address> findAllByUserId(UserId userId) {
        return jpaRepository.findAllByUserId(userId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Address> findByIdAndUserId(UUID id, UserId userId) {
        return jpaRepository.findByIdAndUserId(id, userId.value()).map(this::toDomain);
    }

    @Override
    public Address save(Address address) {
        var entity = jpaRepository.findById(address.getId()).orElse(new AddressEntity());
        entity.setId(address.getId());
        entity.setUserId(address.getUserId().value());
        entity.setAlias(address.getAlias());
        entity.setZipCode(address.getZipCode());
        entity.setStreet(address.getStreet());
        entity.setNumber(address.getNumber());
        entity.setComplement(address.getComplement());
        entity.setNeighborhood(address.getNeighborhood());
        entity.setCity(address.getCity());
        entity.setState(address.getState());
        entity.setDefault(address.isDefault());
        entity.setCreatedAt(address.getCreatedAt());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public int countByUserId(UserId userId) {
        return jpaRepository.countByUserId(userId.value());
    }

    @Override
    public void clearDefaultByUserId(UserId userId) {
        jpaRepository.clearDefaultByUserId(userId.value());
    }

    private Address toDomain(AddressEntity e) {
        return Address.reconstitute(
                e.getId(), UserId.of(e.getUserId()), e.getAlias(), e.getZipCode(),
                e.getStreet(), e.getNumber(), e.getComplement(), e.getNeighborhood(),
                e.getCity(), e.getState(), e.isDefault(), e.getCreatedAt()
        );
    }
}
