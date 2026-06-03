package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CannotRemovePrincipalImageException;
import br.com.miriageekstore.catalog.domain.exception.ProductImageNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.port.in.DeleteProductImageCommand;
import br.com.miriageekstore.catalog.domain.port.in.DeleteProductImageUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import br.com.miriageekstore.catalog.domain.port.out.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProductImageUseCaseImpl implements DeleteProductImageUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final StoragePort storagePort;

    @Value("${minio.bucket-products}")
    private String bucket;

    @Override
    @Transactional
    public void execute(DeleteProductImageCommand command) {
        var productId = ProductId.of(command.productId());
        if (productRepository.findById(productId).isEmpty()) {
            throw new ProductNotFoundException();
        }

        var imageId = ImageId.of(command.imageId());
        var image = imageRepository.findById(imageId)
                .orElseThrow(ProductImageNotFoundException::new);

        if (!image.getProductId().equals(productId)) {
            throw new ProductImageNotFoundException();
        }

        if (image.isPrincipal() && imageRepository.countByProductId(productId) > 1) {
            throw new CannotRemovePrincipalImageException();
        }

        storagePort.deleteFile(bucket, image.getFilename());
        imageRepository.deleteById(imageId);
    }
}
