package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductImageNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.port.in.ProductImageResult;
import br.com.miriageekstore.catalog.domain.port.in.ReorderProductImagesCommand;
import br.com.miriageekstore.catalog.domain.port.in.ReorderProductImagesUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReorderProductImagesUseCaseImpl implements ReorderProductImagesUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;

    @Override
    @Transactional
    public List<ProductImageResult> execute(ReorderProductImagesCommand command) {
        var productId = ProductId.of(command.productId());
        if (productRepository.findById(productId).isEmpty()) {
            throw new ProductNotFoundException();
        }

        var images = imageRepository.findAllByProductId(productId);
        Map<UUID, br.com.miriageekstore.catalog.domain.model.ProductImage> imageMap = images.stream()
                .collect(Collectors.toMap(img -> img.getId().value(), img -> img));

        command.items().forEach(item -> {
            var image = imageMap.get(item.imageId());
            if (image == null) throw new ProductImageNotFoundException();
            image.setImageOrder(item.order());
            imageRepository.save(image);
        });

        return imageRepository.findAllByProductId(productId).stream()
                .map(img -> new ProductImageResult(
                        img.getId().value(), img.getUrl(),
                        img.isPrincipal(), img.getImageOrder(), img.getCreatedAt()))
                .toList();
    }
}
