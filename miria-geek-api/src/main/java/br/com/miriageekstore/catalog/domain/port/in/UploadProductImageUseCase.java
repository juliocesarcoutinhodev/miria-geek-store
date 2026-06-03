package br.com.miriageekstore.catalog.domain.port.in;

public interface UploadProductImageUseCase {
    ProductImageResult execute(UploadProductImageCommand command);
}
