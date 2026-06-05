package br.com.miriageekstore.order.domain.port.out;

import br.com.miriageekstore.order.domain.port.in.AdminOrderQuery;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailResult;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersResult;
import br.com.miriageekstore.order.domain.port.in.OrderSummaryResult;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AdminOrderRepository {
    ListAdminOrdersResult searchForAdmin(AdminOrderQuery query);
    Optional<GetAdminOrderDetailResult> findByIdForAdmin(UUID id);
    OrderSummaryResult getSummary(Instant startDate, Instant endDate);
}
