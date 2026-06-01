package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressLimitExceededException;
import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.AddressCommand;
import br.com.miriageekstore.identity.domain.port.in.AddressResult;
import br.com.miriageekstore.identity.domain.port.in.CreateAddressUseCase;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAddressUseCaseImpl implements CreateAddressUseCase {

    private static final int MAX_ADDRESSES = 5;

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResult execute(UserId userId, AddressCommand command) {
        if (addressRepository.countByUserId(userId) >= MAX_ADDRESSES) {
            throw new AddressLimitExceededException();
        }

        if (command.isDefault()) {
            addressRepository.clearDefaultByUserId(userId);
        }

        var address = Address.create(userId,
                command.alias(), command.zipCode(), command.street(), command.number(),
                command.complement(), command.neighborhood(), command.city(), command.state(),
                command.isDefault());

        return AddressMapper.toResult(addressRepository.save(address));
    }
}
