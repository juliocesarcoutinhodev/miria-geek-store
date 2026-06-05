package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailResult;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailUseCase;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminOrderDetailUseCaseTest {

    @Mock AdminOrderRepository repository;
    @InjectMocks GetAdminOrderDetailUseCaseImpl useCase;

    @Test
    void shouldReturnOrderDetailWhenFound() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "PAID");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.status()).isEqualTo("PAID");
        assertThat(result.orderNumber()).isEqualTo("ORD-00000001");
        verify(repository).findByIdForAdmin(id);
    }

    @Test
    void shouldThrowOrderNotFoundWhenOrderDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Pedido não encontrado");
    }

    @Test
    void shouldReturnCustomerInfo() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "PAID");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.customer().name()).isEqualTo("Test Customer");
        assertThat(result.customer().email()).isEqualTo("customer@test.com");
    }

    @Test
    void shouldReturnItemsWithDetails() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "PAID");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).productName()).isEqualTo("Funko Pop Batman");
        assertThat(result.items().get(0).quantity()).isEqualTo(2);
        assertThat(result.items().get(0).subtotal()).isEqualByComparingTo("199.80");
    }

    @Test
    void shouldReturnNullDeliveryAddressWhenNotSet() {
        var id     = UUID.randomUUID();
        var detail = detailWithoutAddress(id);
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.deliveryAddress()).isNull();
    }

    @Test
    void shouldReturnNullPaymentWhenNotYetProcessed() {
        var id     = UUID.randomUUID();
        var detail = detailWithoutPayment(id);
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.payment()).isNull();
    }

    @Test
    void shouldReturnStatusHistoryOrderedChronologically() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "SHIPPED");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.statusHistory()).hasSize(2);
        assertThat(result.statusHistory().get(0).status()).isEqualTo("PENDING_PAYMENT");
        assertThat(result.statusHistory().get(1).status()).isEqualTo("SHIPPED");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private GetAdminOrderDetailResult detail(UUID id, String status) {
        var customer = new GetAdminOrderDetailResult.CustomerInfo(
                UUID.randomUUID(), "Test Customer", "customer@test.com");

        var item = new GetAdminOrderDetailResult.OrderItemInfo(
                "Funko Pop Batman", "Color", "Black", "SKU-001",
                "http://localhost:9000/miria-products/batman.jpg",
                new BigDecimal("99.90"), 2, new BigDecimal("199.80"));

        var address = new GetAdminOrderDetailResult.DeliveryAddressInfo(
                "Home", "Rua das Flores", "123", null,
                "Centro", "São Paulo", "SP", "01310-100");

        var payment = new GetAdminOrderDetailResult.PaymentInfo(
                UUID.randomUUID(), "PAID", "CREDIT_CARD",
                new BigDecimal("199.80"), Instant.now(), "stripe");

        var now = Instant.now();
        var history = List.of(
                new GetAdminOrderDetailResult.StatusHistoryEntry(
                        "PENDING_PAYMENT", null, null, null, now.minusSeconds(3600)),
                new GetAdminOrderDetailResult.StatusHistoryEntry(
                        status, "Shipped via Correios", UUID.randomUUID(), "Admin User", now)
        );

        return new GetAdminOrderDetailResult(
                id, "ORD-00000001", status, new BigDecimal("199.80"),
                now.minusSeconds(3600), now,
                customer, List.of(item), address, payment, history);
    }

    private GetAdminOrderDetailResult detailWithoutAddress(UUID id) {
        var base = detail(id, "PENDING_PAYMENT");
        return new GetAdminOrderDetailResult(
                base.id(), base.orderNumber(), base.status(), base.total(),
                base.createdAt(), base.updatedAt(), base.customer(),
                base.items(), null, base.payment(), base.statusHistory());
    }

    private GetAdminOrderDetailResult detailWithoutPayment(UUID id) {
        var base = detail(id, "PENDING_PAYMENT");
        return new GetAdminOrderDetailResult(
                base.id(), base.orderNumber(), base.status(), base.total(),
                base.createdAt(), base.updatedAt(), base.customer(),
                base.items(), base.deliveryAddress(), null, base.statusHistory());
    }
}
