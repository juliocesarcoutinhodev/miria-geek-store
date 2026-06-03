package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.ListPublicCategoriesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Categories", description = "Categorias públicas para a loja")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final ListPublicCategoriesUseCase listPublicCategoriesUseCase;

    @Operation(summary = "Listar categorias ativas (público)")
    @GetMapping
    List<CategoryPublicResponse> listCategories() {
        return listPublicCategoriesUseCase.execute().categories().stream()
                .map(c -> new CategoryPublicResponse(c.id(), c.name(), c.slug(), c.totalProducts()))
                .collect(Collectors.toList());
    }
}
