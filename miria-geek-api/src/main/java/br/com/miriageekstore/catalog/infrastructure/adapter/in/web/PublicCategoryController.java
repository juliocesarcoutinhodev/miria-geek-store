package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.ListPublicCategoriesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categories", description = "Categorias públicas para a loja")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final ListPublicCategoriesUseCase listPublicCategoriesUseCase;
    private final CategoryWebMapper mapper;

    @Operation(summary = "Listar categorias ativas (público)")
    @GetMapping
    List<CategoryPublicResponse> listCategories() {
        return mapper.toPublicResponseList(listPublicCategoriesUseCase.execute());
    }
}
