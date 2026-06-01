package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressLinkedToActiveOrderException;
import br.com.miriageekstore.identity.domain.exception.AddressNotFoundException;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.DeleteAddressUseCase;
import br.com.miriageekstore.identity.domain.port.out.ActiveOrderChecker;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAddressUseCaseImpl implements DeleteAddressUseCase {

    private final AddressRepository addressRepository;
    private final ActiveOrderChecker activeOrderChecker;

    @Override
    @Transactional
    public void execute(UserId userId, UUID addressId) {
        var address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(AddressNotFoundException::new);

        if (activeOrderChecker.hasActiveOrder(address.getId())) {
            throw new AddressLinkedToActiveOrderException();
        }

        addressRepository.delete(addressId);
    }
}
