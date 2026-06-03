package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.ListProductsUseCase;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Products", description = "Catálogo de produtos — público")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ListProductsUseCase listProductsUseCase;
    private final ProductCatalogWebMapper mapper;

    @Operation(
            summary = "Listar produtos do catálogo (público)",
            description = """
                    Retorna produtos ACTIVE com ao menos 1 variante ativa em estoque.

                    Ordenações aceitas: mais_recente (padrão), nome_asc, nome_desc,
                    preco_asc, preco_desc, destaque.

                    Tamanho máximo de página: 100.
                    """
    )
    @GetMapping
    ProductCatalogPageResponse listProducts(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Boolean destaque,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "mais_recente") String sort) {

        var query = new ProductSearchQuery(
                nome, categoriaId, precoMin, precoMax, destaque,
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                sort
        );
        return mapper.toPageResponse(listProductsUseCase.execute(query));
    }
}
