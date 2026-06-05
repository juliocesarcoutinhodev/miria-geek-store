package br.com.miriageekstore.order.domain.port.in;

import java.util.UUID;

public interface GetAdminOrderDetailUseCase {
    GetAdminOrderDetailResult execute(UUID id);
}
