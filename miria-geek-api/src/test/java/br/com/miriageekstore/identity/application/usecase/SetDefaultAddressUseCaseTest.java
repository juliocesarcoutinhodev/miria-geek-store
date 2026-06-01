package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressNotFoundException;
import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetDefaultAddressUseCaseTest {

    @Mock AddressRepository addressRepository;
    @InjectMocks SetDefaultAddressUseCaseImpl useCase;

    @Test
    void shouldClearOldDefaultAndMarkNewOne() {
        var userId = UserId.generate();
        var address = address(userId, false);
        when(addressRepository.findByIdAndUserId(address.getId(), userId)).thenReturn(Optional.of(address));
        when(addressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(userId, address.getId());

        verify(addressRepository).clearDefaultByUserId(userId);
        verify(addressRepository).save(argThat(Address::isDefault));
        assertThat(result.isDefault()).isTrue();
    }

    @Test
    void shouldThrowWhenAddressNotFound() {
        var userId = UserId.generate();
        var id = UUID.randomUUID();
        when(addressRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, id))
                .isInstanceOf(AddressNotFoundException.class);
    }

    private Address address(UserId userId, boolean isDefault) {
        return Address.reconstitute(UUID.randomUUID(), userId, "Casa", "01310-100",
                "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", "SP", isDefault, Instant.now());
    }
}
