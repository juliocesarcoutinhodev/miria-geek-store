package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public interface GetAdminProductDetailUseCase {
    GetAdminProductDetailResult execute(UUID productId);
}
