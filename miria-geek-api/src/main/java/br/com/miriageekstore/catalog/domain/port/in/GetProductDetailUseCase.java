package br.com.miriageekstore.catalog.domain.port.in;

public interface GetProductDetailUseCase {
    GetProductDetailResult execute(String slug);
}
