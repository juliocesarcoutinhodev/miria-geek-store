package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressNotFoundException;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.AddressResult;
import br.com.miriageekstore.identity.domain.port.in.SetDefaultAddressUseCase;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetDefaultAddressUseCaseImpl implements SetDefaultAddressUseCase {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResult execute(UserId userId, UUID addressId) {
        var address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(AddressNotFoundException::new);

        addressRepository.clearDefaultByUserId(userId);
        address.markAsDefault();

        return AddressMapper.toResult(addressRepository.save(address));
    }
}
