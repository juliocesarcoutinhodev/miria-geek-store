package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressLinkedToActiveOrderException;
import br.com.miriageekstore.identity.domain.exception.AddressNotFoundException;
import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.ActiveOrderChecker;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAddressUseCaseTest {

    @Mock AddressRepository addressRepository;
    @Mock ActiveOrderChecker activeOrderChecker;
    @InjectMocks DeleteAddressUseCaseImpl useCase;

    @Test
    void shouldDeleteAddressWhenNoActiveOrder() {
        var userId = UserId.generate();
        var address = address(userId);
        when(addressRepository.findByIdAndUserId(address.getId(), userId)).thenReturn(Optional.of(address));
        when(activeOrderChecker.hasActiveOrder(address.getId())).thenReturn(false);

        useCase.execute(userId, address.getId());

        verify(addressRepository).delete(address.getId());
    }

    @Test
    void shouldThrowWhenAddressLinkedToActiveOrder() {
        var userId = UserId.generate();
        var address = address(userId);
        when(addressRepository.findByIdAndUserId(address.getId(), userId)).thenReturn(Optional.of(address));
        when(activeOrderChecker.hasActiveOrder(address.getId())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(userId, address.getId()))
                .isInstanceOf(AddressLinkedToActiveOrderException.class);

        verify(addressRepository, never()).delete(address.getId());
    }

    @Test
    void shouldThrowWhenAddressNotFound() {
        var userId = UserId.generate();
        var id = UUID.randomUUID();
        when(addressRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, id))
                .isInstanceOf(AddressNotFoundException.class);
    }

    private Address address(UserId userId) {
        return Address.reconstitute(UUID.randomUUID(), userId, "Casa", "01310-100",
                "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", "SP", false, Instant.now());
    }
}
