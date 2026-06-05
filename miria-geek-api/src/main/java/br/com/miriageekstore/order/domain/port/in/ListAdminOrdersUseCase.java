package br.com.miriageekstore.order.domain.port.in;

public interface ListAdminOrdersUseCase {
    ListAdminOrdersResult execute(AdminOrderQuery query);
}
