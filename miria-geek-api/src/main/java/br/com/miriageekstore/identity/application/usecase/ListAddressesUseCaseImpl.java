package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.AddressResult;
import br.com.miriageekstore.identity.domain.port.in.ListAddressesUseCase;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAddressesUseCaseImpl implements ListAddressesUseCase {

    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResult> execute(UserId userId) {
        return addressRepository.findAllByUserId(userId).stream()
                .sorted(Comparator.comparing(Address::isDefault).reversed()
                        .thenComparing(Address::getCreatedAt))
                .map(AddressMapper::toResult)
                .toList();
    }
}
