package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ProductCatalogPersistenceAdapter implements ProductCatalogRepository {

    private final EntityManager em;

    @Override
    public ListProductsResult search(ProductSearchQuery query) {
        String conditions = buildConditions(query);
        String having    = buildHaving(query);

        String baseFrom =
                " FROM products p" +
                " JOIN categories c ON c.id = p.category_id" +
                " JOIN product_variants v ON v.product_id = p.id AND v.active = true AND v.stock > 0" +
                " WHERE p.status = 'ACTIVE'" +
                conditions;

        String countSql =
                "SELECT COUNT(*) FROM (" +
                "SELECT p.id" + baseFrom +
                " GROUP BY p.id" + having +
                ") AS subq";

        String dataSql =
                "SELECT p.id, p.name, p.slug, p.category_id, c.name," +
                " (SELECT img.url FROM product_images img" +
                "  WHERE img.product_id = p.id AND img.principal = true" +
                "  ORDER BY img.image_order ASC LIMIT 1) AS principal_url," +
                " MIN(v.price), MAX(v.price), CAST(COUNT(v.id) AS INTEGER), p.featured" +
                baseFrom +
                " GROUP BY p.id, p.name, p.slug, p.category_id, c.name, p.featured, p.created_at" +
                having +
                " ORDER BY " + resolveOrderBy(query.sort()) +
                " LIMIT :pageSize OFFSET :pageOffset";

        Query countQuery = em.createNativeQuery(countSql);
        Query dataQuery  = em.createNativeQuery(dataSql);

        applyParams(countQuery, query);
        applyParams(dataQuery, query);
        dataQuery.setParameter("pageSize",   query.size());
        dataQuery.setParameter("pageOffset", (long) query.page() * query.size());

        long total = ((Number) countQuery.getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<ListProductsResult.ProductItem> content = rows.stream().map(this::toItem).toList();

        int totalPages = query.size() > 0 ? (int) Math.ceil((double) total / query.size()) : 0;
        return new ListProductsResult(content, query.page(), query.size(), total, totalPages);
    }

    private String buildConditions(ProductSearchQuery query) {
        var sb = new StringBuilder();
        if (query.nome() != null && !query.nome().isBlank()) {
            sb.append(" AND LOWER(p.name) LIKE :nome");
        }
        if (query.categoriaId() != null) {
            sb.append(" AND p.category_id = :categoriaId");
        }
        if (query.destaque() != null) {
            sb.append(" AND p.featured = :destaque");
        }
        return sb.toString();
    }

    private String buildHaving(ProductSearchQuery query) {
        var parts = new ArrayList<String>();
        if (query.precoMin() != null) parts.add("MIN(v.price) >= :precoMin");
        if (query.precoMax() != null) parts.add("MAX(v.price) <= :precoMax");
        if (parts.isEmpty()) return "";
        return " HAVING " + String.join(" AND ", parts);
    }

    private void applyParams(Query q, ProductSearchQuery query) {
        if (query.nome() != null && !query.nome().isBlank()) {
            q.setParameter("nome", "%" + query.nome().toLowerCase() + "%");
        }
        if (query.categoriaId() != null) {
            q.setParameter("categoriaId", query.categoriaId());
        }
        if (query.destaque() != null) {
            q.setParameter("destaque", query.destaque());
        }
        if (query.precoMin() != null) {
            q.setParameter("precoMin", query.precoMin());
        }
        if (query.precoMax() != null) {
            q.setParameter("precoMax", query.precoMax());
        }
    }

    private String resolveOrderBy(String sort) {
        return switch (sort == null ? "mais_recente" : sort) {
            case "nome_asc"   -> "p.name ASC";
            case "nome_desc"  -> "p.name DESC";
            case "preco_asc"  -> "MIN(v.price) ASC";
            case "preco_desc" -> "MAX(v.price) DESC";
            case "destaque"   -> "p.featured DESC, p.created_at DESC";
            default           -> "p.created_at DESC";
        };
    }

    private ListProductsResult.ProductItem toItem(Object[] row) {
        return new ListProductsResult.ProductItem(
                (UUID)          row[0],
                (String)        row[1],
                (String)        row[2],
                (UUID)          row[3],
                (String)        row[4],
                (String)        row[5],
                (BigDecimal)    row[6],
                (BigDecimal)    row[7],
                ((Number)       row[8]).intValue(),
                (Boolean)       row[9]
        );
    }
}
