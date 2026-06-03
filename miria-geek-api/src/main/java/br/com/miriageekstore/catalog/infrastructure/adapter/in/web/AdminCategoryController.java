package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryCommand;
import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryUseCase;
import br.com.miriageekstore.catalog.domain.port.in.DeleteCategoryUseCase;
import br.com.miriageekstore.catalog.domain.port.in.GetCategoryByIdUseCase;
import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesQuery;
import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesUseCase;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Admin Categories", description = "Gestão de categorias — requer ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @Operation(summary = "Listar categorias paginado com filtros",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping
    PagedCategoryResponse listCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        var result = listCategoriesUseCase.execute(
                new ListCategoriesQuery(name, active, page, size, sort, direction));

        var content = result.content().stream()
                .map(c -> new CategoryResponse(
                        c.id(), c.name(), c.slug(), c.description(),
                        c.totalProducts(), c.active(), c.createdAt()))
                .collect(Collectors.toList());

        return new PagedCategoryResponse(
                content, result.page(), result.size(), result.totalElements(), result.totalPages());
    }

    @Operation(summary = "Buscar categoria por ID",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping("/{id}")
    CategoryResponse getCategoryById(@PathVariable UUID id) {
        var result = getCategoryByIdUseCase.execute(CategoryId.of(id));
        return new CategoryResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.totalProducts(), result.active(), result.createdAt());
    }

    @Operation(summary = "Criar nova categoria",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryResponse createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        var result = createCategoryUseCase.execute(
                new CreateCategoryCommand(request.name(), request.description()));
        return new CategoryResponse(
                result.id(), result.name(), result.slug(), result.description(),
                0L, result.active(), result.createdAt());
    }

    @Operation(summary = "Atualizar categoria",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PutMapping("/{id}")
    CategoryResponse updateCategory(@PathVariable UUID id,
                                    @Valid @RequestBody UpdateCategoryRequest request) {
        var result = updateCategoryUseCase.execute(
                new UpdateCategoryCommand(CategoryId.of(id), request.name(),
                        request.description(), request.active()));
        return new CategoryResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.totalProducts(), result.active(), result.createdAt());
    }

    @Operation(summary = "Excluir categoria",
               security = @SecurityRequirement(name = "cookieAuth"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable UUID id) {
        deleteCategoryUseCase.execute(CategoryId.of(id));
    }
}
