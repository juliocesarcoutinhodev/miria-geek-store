package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.AddressLimitExceededException;
import br.com.miriageekstore.identity.domain.model.Address;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.AddressCommand;
import br.com.miriageekstore.identity.domain.port.out.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAddressUseCaseTest {

    @Mock AddressRepository addressRepository;
    @InjectMocks CreateAddressUseCaseImpl useCase;

    private static final AddressCommand CMD = new AddressCommand(
            "Casa", "01310-100", "Av. Paulista", "1000", "Apto 42",
            "Bela Vista", "São Paulo", "SP", true);

    @Test
    void shouldCreateAddressSuccessfully() {
        var userId = UserId.generate();
        when(addressRepository.countByUserId(userId)).thenReturn(2);
        when(addressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(userId, CMD);

        assertThat(result.alias()).isEqualTo("Casa");
        assertThat(result.isDefault()).isTrue();
        verify(addressRepository).clearDefaultByUserId(userId);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void shouldThrowWhenLimitReached() {
        var userId = UserId.generate();
        when(addressRepository.countByUserId(userId)).thenReturn(5);

        assertThatThrownBy(() -> useCase.execute(userId, CMD))
                .isInstanceOf(AddressLimitExceededException.class);

        verify(addressRepository, never()).save(any());
    }

    @Test
    void shouldNotClearDefaultWhenNewAddressIsNotDefault() {
        var userId = UserId.generate();
        var cmd = new AddressCommand("Trabalho", "01310-100", "Av. Paulista", "2000",
                null, "Bela Vista", "São Paulo", "SP", false);
        when(addressRepository.countByUserId(userId)).thenReturn(1);
        when(addressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(userId, cmd);

        verify(addressRepository, never()).clearDefaultByUserId(any());
    }
}
