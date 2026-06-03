package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailResult;
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

    ProductDetailResponse toDetailResponse(GetProductDetailResult result) {
        var imagens = result.images().stream()
                .map(img -> new ProductDetailResponse.ImagemInfo(
                        img.id(), img.url(), img.principal(), img.imageOrder()))
                .toList();

        var variantes = result.variants().stream()
                .map(v -> new ProductDetailResponse.VarianteInfo(
                        v.id(), v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(), v.active(), v.available()))
                .toList();

        return new ProductDetailResponse(
                result.id(), result.name(), result.slug(), result.description(),
                new ProductDetailResponse.CategoriaInfo(result.categoryId(), result.categoryName()),
                result.status(), result.featured(), result.createdAt(),
                imagens, variantes);
    }
}
