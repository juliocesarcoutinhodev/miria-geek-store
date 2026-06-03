package br.com.miriageekstore.catalog.domain.port.in;

public interface SetPrincipalImageUseCase {
    ProductImageResult execute(SetPrincipalImageCommand command);
}
