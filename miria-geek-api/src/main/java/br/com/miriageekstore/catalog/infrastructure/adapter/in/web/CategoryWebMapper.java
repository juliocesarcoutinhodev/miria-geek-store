package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryResult;
import br.com.miriageekstore.catalog.domain.port.in.GetCategoryByIdResult;
import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesResult;
import br.com.miriageekstore.catalog.domain.port.in.ListPublicCategoriesResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class CategoryWebMapper {

    CategoryResponse toResponse(GetCategoryByIdResult result) {
        return new CategoryResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.totalProducts(), result.active(), result.createdAt());
    }

    CategoryResponse toResponse(CreateCategoryResult result) {
        return new CategoryResponse(
                result.id(), result.name(), result.slug(), result.description(),
                0L, result.active(), result.createdAt());
    }

    CategoryResponse toResponse(UpdateCategoryResult result) {
        return new CategoryResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.totalProducts(), result.active(), result.createdAt());
    }

    PagedCategoryResponse toPagedResponse(ListCategoriesResult result) {
        List<CategoryResponse> content = result.content().stream()
                .map(s -> new CategoryResponse(
                        s.id(), s.name(), s.slug(), s.description(),
                        s.totalProducts(), s.active(), s.createdAt()))
                .toList();
        return new PagedCategoryResponse(
                content, result.page(), result.size(),
                result.totalElements(), result.totalPages());
    }

    List<CategoryPublicResponse> toPublicResponseList(ListPublicCategoriesResult result) {
        return result.categories().stream()
                .map(s -> new CategoryPublicResponse(s.id(), s.name(), s.slug(), s.totalProducts()))
                .toList();
    }
}
