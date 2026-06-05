package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.event.OrderTrackingUpdated;
import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.exception.OrderNotShippedException;
import br.com.miriageekstore.order.domain.model.Carrier;
import br.com.miriageekstore.order.domain.model.Order;
import br.com.miriageekstore.order.domain.model.OrderId;
import br.com.miriageekstore.order.domain.model.OrderStatus;
import br.com.miriageekstore.order.domain.port.in.UpdateTrackingCommand;
import br.com.miriageekstore.order.domain.port.out.OrderEventPublisher;
import br.com.miriageekstore.order.domain.port.out.OrderWriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTrackingUseCaseTest {

    @Mock OrderWriteRepository orderWriteRepository;
    @Mock OrderEventPublisher  eventPublisher;
    @InjectMocks UpdateTrackingUseCaseImpl useCase;

    @Test
    void shouldSaveTrackingAndReturnResult() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id))).thenReturn(Optional.of(shipped(id)));

        var result = useCase.execute(new UpdateTrackingCommand(
                id, "BR123456789BR", Carrier.CORREIOS, null, null));

        assertThat(result.trackingCode()).isEqualTo("BR123456789BR");
        assertThat(result.carrier()).isEqualTo("CORREIOS");
        assertThat(result.trackingUrl()).contains("BR123456789BR");
        assertThat(result.orderNumber()).isEqualTo("ORD-00000001");
        verify(orderWriteRepository).saveTracking(
                eq(OrderId.of(id)), eq("BR123456789BR"), eq(Carrier.CORREIOS), any(), any());
    }

    @Test
    void shouldGenerateCorrectUrlForCorreios() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(any())).thenReturn(Optional.of(shipped(id)));

        var result = useCase.execute(new UpdateTrackingCommand(
                id, "BR123456789BR", Carrier.CORREIOS, null, null));

        assertThat(result.trackingUrl())
                .isEqualTo("https://rastreamento.correios.com.br/app/index.php?objeto=BR123456789BR");
    }

    @Test
    void shouldGenerateCorrectUrlForJadlog() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(any())).thenReturn(Optional.of(shipped(id)));

        var result = useCase.execute(new UpdateTrackingCommand(
                id, "CTE123456", Carrier.JADLOG, null, null));

        assertThat(result.trackingUrl())
                .isEqualTo("https://www.jadlog.com.br/jadlog/tracking.jad?cte=CTE123456");
    }

    @Test
    void shouldUseCustomUrlForOutro() {
        var id  = UUID.randomUUID();
        var url = "https://transportadora-xyz.com/rastrear?cod=ABC123";
        when(orderWriteRepository.findById(any())).thenReturn(Optional.of(shipped(id)));

        var result = useCase.execute(new UpdateTrackingCommand(
                id, "ABC123", Carrier.OUTRO, "Transportadora XYZ", url));

        assertThat(result.trackingUrl()).isEqualTo(url);
        assertThat(result.carrierName()).isEqualTo("Transportadora XYZ");
    }

    @Test
    void shouldPublishOrderTrackingUpdatedEvent() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(any())).thenReturn(Optional.of(shipped(id)));

        useCase.execute(new UpdateTrackingCommand(
                id, "BR123456789BR", Carrier.CORREIOS, null, null));

        var captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());

        var event = (OrderTrackingUpdated) captor.getValue();
        assertThat(event.orderId()).isEqualTo(id);
        assertThat(event.trackingCode()).isEqualTo("BR123456789BR");
        assertThat(event.carrier()).isEqualTo("CORREIOS");
        assertThat(event.customerEmail()).isEqualTo("customer@test.com");
        assertThat(event.orderNumber()).isEqualTo("ORD-00000001");
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderWriteRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new UpdateTrackingCommand(
                UUID.randomUUID(), "BR123456789BR", Carrier.CORREIOS, null, null)))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void shouldThrowWhenOrderIsNotShipped() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PAID)));

        assertThatThrownBy(() -> useCase.execute(new UpdateTrackingCommand(
                id, "BR123456789BR", Carrier.CORREIOS, null, null)))
                .isInstanceOf(OrderNotShippedException.class)
                .hasMessageContaining("PAID");

        verify(orderWriteRepository, never()).saveTracking(any(), any(), any(), any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenOutroHasNoCustomUrl() {
        assertThatThrownBy(() -> useCase.execute(new UpdateTrackingCommand(
                UUID.randomUUID(), "ABC123", Carrier.OUTRO, "XYZ", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("urlRastreamentoCustom");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Order shipped(UUID id) {
        return order(id, OrderStatus.SHIPPED);
    }

    private Order order(UUID id, OrderStatus status) {
        return Order.reconstitute(OrderId.of(id), status, UUID.randomUUID(),
                "ORD-00000001", "customer@test.com", "Test Customer", Instant.now());
    }
}
