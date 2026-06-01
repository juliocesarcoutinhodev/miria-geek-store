package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressNotFoundException;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.AddressCommand;
import br.com.miriageekstore.identity.domain.port.in.AddressResult;
import br.com.miriageekstore.identity.domain.port.in.UpdateAddressUseCase;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateAddressUseCaseImpl implements UpdateAddressUseCase {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResult execute(UserId userId, UUID addressId, AddressCommand command) {
        var address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(AddressNotFoundException::new);

        if (command.isDefault() && !address.isDefault()) {
            addressRepository.clearDefaultByUserId(userId);
            address.markAsDefault();
        } else if (!command.isDefault() && address.isDefault()) {
            address.clearDefault();
        }

        address.update(command.alias(), command.zipCode(), command.street(), command.number(),
                command.complement(), command.neighborhood(), command.city(), command.state());

        return AddressMapper.toResult(addressRepository.save(address));
    }
}
