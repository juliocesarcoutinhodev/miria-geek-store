package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import org.springframework.stereotype.Component;

@Component
class ProductCatalogWebMapper {

    ProductCatalogResponse toResponse(ListProductsResult.ProductItem item) {
        return new ProductCatalogResponse(
                item.id(),
                item.name(),
                item.slug(),
                new ProductCatalogResponse.CategoriaInfo(item.categoryId(), item.categoryName()),
                item.principalImageUrl(),
                item.minPrice(),
                item.maxPrice(),
                item.totalActiveVariants(),
                item.featured()
        );
    }

    ProductCatalogPageResponse toPageResponse(ListProductsResult result) {
        var content = result.content().stream().map(this::toResponse).toList();
        return new ProductCatalogPageResponse(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
