package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductImageNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.port.in.ProductImageResult;
import br.com.miriageekstore.catalog.domain.port.in.SetPrincipalImageCommand;
import br.com.miriageekstore.catalog.domain.port.in.SetPrincipalImageUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetPrincipalImageUseCaseImpl implements SetPrincipalImageUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;

    @Override
    @Transactional
    public ProductImageResult execute(SetPrincipalImageCommand command) {
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

        imageRepository.findPrincipalByProductId(productId).ifPresent(current -> {
            if (!current.getId().equals(imageId)) {
                current.setPrincipal(false);
                imageRepository.save(current);
            }
        });

        image.setPrincipal(true);
        var saved = imageRepository.save(image);

        return new ProductImageResult(
                saved.getId().value(), saved.getUrl(),
                saved.isPrincipal(), saved.getImageOrder(), saved.getCreatedAt()
        );
    }
}
