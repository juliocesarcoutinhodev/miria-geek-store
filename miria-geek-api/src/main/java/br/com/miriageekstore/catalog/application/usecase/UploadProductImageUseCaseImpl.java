package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ImageLimitExceededException;
import br.com.miriageekstore.catalog.domain.exception.InvalidImageTypeException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;
import br.com.miriageekstore.catalog.domain.port.in.ProductImageResult;
import br.com.miriageekstore.catalog.domain.port.in.UploadProductImageCommand;
import br.com.miriageekstore.catalog.domain.port.in.UploadProductImageUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import br.com.miriageekstore.catalog.domain.port.out.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadProductImageUseCaseImpl implements UploadProductImageUseCase {

    private static final int MAX_IMAGES = 10;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final StoragePort storagePort;

    @Value("${minio.bucket-products}")
    private String bucket;

    @Override
    @Transactional
    public ProductImageResult execute(UploadProductImageCommand command) {
        if (!ALLOWED_TYPES.contains(command.contentType())) {
            throw new InvalidImageTypeException(command.contentType());
        }

        var productId = ProductId.of(command.productId());
        if (productRepository.findById(productId).isEmpty()) {
            throw new ProductNotFoundException();
        }

        long imageCount = imageRepository.countByProductId(productId);
        if (imageCount >= MAX_IMAGES) {
            throw new ImageLimitExceededException();
        }

        var filename = command.productId() + "-" + UUID.randomUUID() + "." + extensionFor(command.contentType());
        var url = storagePort.uploadFile(bucket, filename, command.inputStream(), command.contentType());

        boolean isPrincipal = imageCount == 0;
        var image = ProductImage.create(productId, url, filename, isPrincipal, (int) imageCount);
        var saved = imageRepository.save(image);

        return toResult(saved);
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }

    private ProductImageResult toResult(ProductImage image) {
        return new ProductImageResult(
                image.getId().value(), image.getUrl(),
                image.isPrincipal(), image.getImageOrder(), image.getCreatedAt()
        );
    }
}
