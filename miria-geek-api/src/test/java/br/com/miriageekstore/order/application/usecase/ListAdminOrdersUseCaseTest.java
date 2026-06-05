package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.port.in.AdminOrderQuery;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersResult;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAdminOrdersUseCaseTest {

    @Mock AdminOrderRepository repository;
    @InjectMocks ListAdminOrdersUseCaseImpl useCase;

    @Test
    void shouldDelegateToRepository() {
        var query    = new AdminOrderQuery(null, null, null, null, null, null, null, null, 0, 20, "latest");
        var expected = new ListAdminOrdersResult(List.of(), 0, 20, 0, 0);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result).isEqualTo(expected);
        verify(repository).searchForAdmin(query);
    }

    @Test
    void shouldReturnOrdersWithAllStatuses() {
        var query = new AdminOrderQuery(null, null, null, null, null, null, null, null, 0, 20, "latest");

        var paid      = item("PAID");
        var cancelled = item("CANCELLED");
        var expected  = new ListAdminOrdersResult(List.of(paid, cancelled), 0, 20, 2, 1);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).extracting(ListAdminOrdersResult.AdminOrderItem::status)
                .containsExactlyInAnyOrder("PAID", "CANCELLED");
    }

    @Test
    void shouldFilterByStatus() {
        var query    = new AdminOrderQuery(null, null, null, "PENDING_PAYMENT", null, null, null, null, 0, 20, "latest");
        var expected = new ListAdminOrdersResult(List.of(item("PENDING_PAYMENT")), 0, 20, 1, 1);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).status()).isEqualTo("PENDING_PAYMENT");
    }

    @Test
    void shouldReturnPaginationMetadata() {
        var query    = new AdminOrderQuery(null, null, null, null, null, null, null, null, 2, 10, "latest");
        var expected = new ListAdminOrdersResult(List.of(), 2, 10, 25, 3);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(25);
        assertThat(result.totalPages()).isEqualTo(3);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private ListAdminOrdersResult.AdminOrderItem item(String status) {
        return new ListAdminOrdersResult.AdminOrderItem(
                UUID.randomUUID(),
                "ORD-00000001",
                status,
                "Test Customer",
                "customer@test.com",
                2,
                new BigDecimal("199.90"),
                Instant.now(),
                Instant.now()
        );
    }
}
