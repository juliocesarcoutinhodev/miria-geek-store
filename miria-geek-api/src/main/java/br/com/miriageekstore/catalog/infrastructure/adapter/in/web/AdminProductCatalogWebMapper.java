package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.GetAdminProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.ListAdminProductsResult;
import org.springframework.stereotype.Component;

@Component
class AdminProductCatalogWebMapper {

    AdminProductPageResponse toPageResponse(ListAdminProductsResult result) {
        var content = result.content().stream().map(this::toSummaryResponse).toList();
        return new AdminProductPageResponse(content, result.page(), result.size(),
                result.totalElements(), result.totalPages());
    }

    private AdminProductSummaryResponse toSummaryResponse(ListAdminProductsResult.AdminProductItem item) {
        return new AdminProductSummaryResponse(
                item.id(),
                item.name(),
                item.slug(),
                new AdminProductSummaryResponse.CategoryInfo(item.categoryId(), item.categoryName()),
                item.status(),
                item.featured(),
                item.totalVariants(),
                item.totalImages(),
                item.totalStock(),
                item.principalImageUrl(),
                item.createdAt()
        );
    }

    AdminProductDetailResponse toDetailResponse(GetAdminProductDetailResult result) {
        var images = result.images().stream()
                .map(img -> new AdminProductDetailResponse.ImageInfo(
                        img.id(), img.url(), img.principal(), img.imageOrder()))
                .toList();

        var variants = result.variants().stream()
                .map(v -> new AdminProductDetailResponse.VariantInfo(
                        v.id(), v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(), v.active(), v.createdAt(),
                        v.weight(), v.width(), v.height(), v.depth()))
                .toList();

        return new AdminProductDetailResponse(
                result.id(), result.name(), result.slug(), result.description(),
                new AdminProductDetailResponse.CategoryInfo(result.categoryId(), result.categoryName()),
                result.status(), result.featured(), result.createdAt(),
                images, variants);
    }
}
