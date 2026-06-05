package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.event.OrderCancelled;
import br.com.miriageekstore.order.domain.event.OrderStatusChanged;
import br.com.miriageekstore.order.domain.exception.InvalidOrderStatusTransitionException;
import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.model.Order;
import br.com.miriageekstore.order.domain.model.OrderId;
import br.com.miriageekstore.order.domain.model.OrderStatus;
import br.com.miriageekstore.order.domain.port.in.UpdateOrderStatusCommand;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateOrderStatusUseCaseTest {

    @Mock OrderWriteRepository orderWriteRepository;
    @Mock OrderEventPublisher  eventPublisher;
    @InjectMocks UpdateOrderStatusUseCaseImpl useCase;

    // ── valid transitions ─────────────────────────────────────────────────────

    @Test
    void shouldTransitionFromPendingPaymentToPaid() {
        var id      = UUID.randomUUID();
        var adminId = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PENDING_PAYMENT)));

        var result = useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.PAID, null, adminId));

        assertThat(result.previousStatus()).isEqualTo("PENDING_PAYMENT");
        assertThat(result.newStatus()).isEqualTo("PAID");
        verify(orderWriteRepository).updateStatus(eq(OrderId.of(id)), eq(OrderStatus.PAID), any());
        verify(orderWriteRepository).saveHistoryEntry(any(), eq(OrderId.of(id)), eq(OrderStatus.PAID), any(), eq(adminId), any());
    }

    @Test
    void shouldTransitionFromPaidToPreparing() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PAID)));

        var result = useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.PREPARING, null, UUID.randomUUID()));

        assertThat(result.newStatus()).isEqualTo("PREPARING");
    }

    @Test
    void shouldTransitionFromPreparingToShipped() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PREPARING)));

        var result = useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.SHIPPED, null, UUID.randomUUID()));

        assertThat(result.newStatus()).isEqualTo("SHIPPED");
    }

    @Test
    void shouldTransitionFromShippedToDelivered() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.SHIPPED)));

        var result = useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.DELIVERED, null, UUID.randomUUID()));

        assertThat(result.newStatus()).isEqualTo("DELIVERED");
    }

    // ── cancellation ──────────────────────────────────────────────────────────

    @Test
    void shouldCancelFromPendingPaymentAndRestoreStock() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PENDING_PAYMENT)));

        useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.CANCELLED, "Customer request", UUID.randomUUID()));

        verify(orderWriteRepository).restoreStockForOrder(OrderId.of(id));
    }

    @Test
    void shouldCancelFromPaidAndRestoreStock() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PAID)));

        useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.CANCELLED, null, UUID.randomUUID()));

        verify(orderWriteRepository).restoreStockForOrder(OrderId.of(id));
    }

    @Test
    void shouldPublishOrderCancelledEventOnCancellation() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PENDING_PAYMENT)));

        useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.CANCELLED, "Test note", UUID.randomUUID()));

        var captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publish(captor.capture());

        var events = captor.getAllValues();
        assertThat(events).anyMatch(e -> e instanceof OrderCancelled);
        assertThat(events).anyMatch(e -> e instanceof OrderStatusChanged);

        var cancelled = (OrderCancelled) events.stream()
                .filter(e -> e instanceof OrderCancelled).findFirst().orElseThrow();
        assertThat(cancelled.orderId()).isEqualTo(id);
        assertThat(cancelled.previousStatus()).isEqualTo("PENDING_PAYMENT");
        assertThat(cancelled.note()).isEqualTo("Test note");
    }

    // ── events ────────────────────────────────────────────────────────────────

    @Test
    void shouldPublishOrderStatusChangedEvent() {
        var id      = UUID.randomUUID();
        var adminId = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PAID)));

        useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.PREPARING, "Moving to prep", adminId));

        var captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());

        var event = (OrderStatusChanged) captor.getValue();
        assertThat(event.orderId()).isEqualTo(id);
        assertThat(event.previousStatus()).isEqualTo("PAID");
        assertThat(event.newStatus()).isEqualTo("PREPARING");
        assertThat(event.note()).isEqualTo("Moving to prep");
    }

    @Test
    void shouldNotPublishCancelledEventForNonCancellation() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PAID)));

        useCase.execute(new UpdateOrderStatusCommand(id, OrderStatus.PREPARING, null, UUID.randomUUID()));

        var captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publish(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(OrderStatusChanged.class);
        assertThat(captor.getValue()).isNotInstanceOf(OrderCancelled.class);
    }

    // ── invalid transitions ───────────────────────────────────────────────────

    @Test
    void shouldRejectTransitionFromDeliveredToAnyStatus() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.DELIVERED)));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateOrderStatusCommand(id, OrderStatus.CANCELLED, null, UUID.randomUUID())))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("DELIVERED")
                .hasMessageContaining("CANCELLED");

        verify(orderWriteRepository, never()).updateStatus(any(), any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldRejectTransitionFromPreparingToCancelled() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.PREPARING)));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateOrderStatusCommand(id, OrderStatus.CANCELLED, null, UUID.randomUUID())))
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
    }

    @Test
    void shouldRejectTransitionFromShippedToCancelled() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id)))
                .thenReturn(Optional.of(order(id, OrderStatus.SHIPPED)));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateOrderStatusCommand(id, OrderStatus.CANCELLED, null, UUID.randomUUID())))
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
    }

    @Test
    void shouldThrowOrderNotFoundWhenOrderDoesNotExist() {
        var id = UUID.randomUUID();
        when(orderWriteRepository.findById(OrderId.of(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new UpdateOrderStatusCommand(id, OrderStatus.PAID, null, UUID.randomUUID())))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Order order(UUID id, OrderStatus status) {
        return Order.reconstitute(OrderId.of(id), status, UUID.randomUUID(), Instant.now());
    }
}
