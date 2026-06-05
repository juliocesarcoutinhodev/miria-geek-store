package br.com.miriageekstore.order.domain.port.out;

import br.com.miriageekstore.order.domain.port.in.AdminOrderQuery;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailResult;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersResult;

import java.util.Optional;
import java.util.UUID;

public interface AdminOrderRepository {
    ListAdminOrdersResult searchForAdmin(AdminOrderQuery query);
    Optional<GetAdminOrderDetailResult> findByIdForAdmin(UUID id);
}
