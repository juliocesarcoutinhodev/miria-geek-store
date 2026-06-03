package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.ProductStatus;
import br.com.miriageekstore.catalog.domain.port.in.AdminProductQuery;
import br.com.miriageekstore.catalog.domain.port.in.GetAdminProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.ListAdminProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ProductCatalogPersistenceAdapter implements ProductCatalogRepository {

    private final EntityManager em;
    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductImageJpaRepository productImageJpaRepository;

    // ── Public listing ────────────────────────────────────────────────────────

    @Override
    public ListProductsResult search(ProductSearchQuery query) {
        String conditions = buildPublicConditions(query);
        String having     = buildHaving(query);

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
                " ORDER BY " + resolvePublicOrderBy(query.sort()) +
                " LIMIT :pageSize OFFSET :pageOffset";

        Query countQuery = em.createNativeQuery(countSql);
        Query dataQuery  = em.createNativeQuery(dataSql);

        applyPublicParams(countQuery, query);
        applyPublicParams(dataQuery, query);
        dataQuery.setParameter("pageSize",   query.size());
        dataQuery.setParameter("pageOffset", (long) query.page() * query.size());

        long total = ((Number) countQuery.getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<ListProductsResult.ProductItem> content = rows.stream().map(this::toPublicItem).toList();

        int totalPages = query.size() > 0 ? (int) Math.ceil((double) total / query.size()) : 0;
        return new ListProductsResult(content, query.page(), query.size(), total, totalPages);
    }

    private String buildPublicConditions(ProductSearchQuery query) {
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

    private void applyPublicParams(Query q, ProductSearchQuery query) {
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

    private String resolvePublicOrderBy(String sort) {
        return switch (sort == null ? "mais_recente" : sort) {
            case "nome_asc"   -> "p.name ASC";
            case "nome_desc"  -> "p.name DESC";
            case "preco_asc"  -> "MIN(v.price) ASC";
            case "preco_desc" -> "MAX(v.price) DESC";
            case "destaque"   -> "p.featured DESC, p.created_at DESC";
            default           -> "p.created_at DESC";
        };
    }

    private ListProductsResult.ProductItem toPublicItem(Object[] row) {
        return new ListProductsResult.ProductItem(
                (UUID)       row[0],
                (String)     row[1],
                (String)     row[2],
                (UUID)       row[3],
                (String)     row[4],
                (String)     row[5],
                (BigDecimal) row[6],
                (BigDecimal) row[7],
                ((Number)    row[8]).intValue(),
                (Boolean)    row[9]
        );
    }

    // ── Public detail (by slug) ───────────────────────────────────────────────

    @Override
    public Optional<GetProductDetailResult> findBySlug(String slug) {
        var productOpt = productJpaRepository.findBySlug(slug);
        if (productOpt.isEmpty() || productOpt.get().getStatus() != ProductStatus.ACTIVE) {
            return Optional.empty();
        }

        var product = productOpt.get();

        String categoryName = categoryJpaRepository.findById(product.getCategoryId())
                .map(CategoryEntity::getName)
                .orElse("");

        var images = productImageJpaRepository
                .findAllByProductIdOrderByImageOrderAsc(product.getId())
                .stream()
                .map(img -> new GetProductDetailResult.ImageItem(
                        img.getId(), img.getUrl(), img.isPrincipal(), img.getImageOrder()))
                .toList();

        var variants = product.getVariants().stream()
                .filter(ProductVariantEntity::isActive)
                .map(v -> new GetProductDetailResult.VariantItem(
                        v.getId(), v.getAttributeName(), v.getAttributeValue(),
                        v.getPrice(), v.getStock(), v.getSku(), v.isActive(), v.getStock() > 0))
                .toList();

        return Optional.of(new GetProductDetailResult(
                product.getId(), product.getName(), product.getSlug(),
                product.getDescription(), product.getCategoryId(), categoryName,
                product.getStatus().name(), product.isFeatured(), product.getCreatedAt(),
                images, variants));
    }

    // ── Admin listing ─────────────────────────────────────────────────────────

    @Override
    public ListAdminProductsResult searchForAdmin(AdminProductQuery query) {
        String conditions = buildAdminConditions(query);

        // Count: only filters on products table — no JOINs needed
        String countSql = "SELECT COUNT(*) FROM products p WHERE 1=1" + conditions;

        // Data: LEFT JOINs to compute aggregates
        String dataSql =
                "SELECT p.id, p.name, p.slug, p.category_id, c.name," +
                " p.status, p.featured, p.created_at," +
                " CAST(COUNT(DISTINCT v.id) AS INTEGER)," +
                " CAST(COUNT(DISTINCT img.id) AS INTEGER)," +
                " COALESCE(SUM(v.stock), 0)" +
                " FROM products p" +
                " JOIN categories c ON c.id = p.category_id" +
                " LEFT JOIN product_variants v ON v.product_id = p.id" +
                " LEFT JOIN product_images img ON img.product_id = p.id" +
                " WHERE 1=1" + conditions +
                " GROUP BY p.id, p.name, p.slug, p.category_id, c.name, p.status, p.featured, p.created_at" +
                " ORDER BY " + resolveAdminOrderBy(query.sort()) +
                " LIMIT :pageSize OFFSET :pageOffset";

        Query countQuery = em.createNativeQuery(countSql);
        Query dataQuery  = em.createNativeQuery(dataSql);

        applyAdminParams(countQuery, query);
        applyAdminParams(dataQuery, query);
        dataQuery.setParameter("pageSize",   query.size());
        dataQuery.setParameter("pageOffset", (long) query.page() * query.size());

        long total = ((Number) countQuery.getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<ListAdminProductsResult.AdminProductItem> content = rows.stream()
                .map(this::toAdminItem)
                .toList();

        int totalPages = query.size() > 0 ? (int) Math.ceil((double) total / query.size()) : 0;
        return new ListAdminProductsResult(content, query.page(), query.size(), total, totalPages);
    }

    private String buildAdminConditions(AdminProductQuery query) {
        var sb = new StringBuilder();
        if (query.nome() != null && !query.nome().isBlank()) {
            sb.append(" AND LOWER(p.name) LIKE :nome");
        }
        if (query.categoriaId() != null) {
            sb.append(" AND p.category_id = :categoriaId");
        }
        if (query.status() != null && !query.status().isBlank()) {
            sb.append(" AND p.status = :status");
        }
        if (query.destaque() != null) {
            sb.append(" AND p.featured = :destaque");
        }
        return sb.toString();
    }

    private void applyAdminParams(Query q, AdminProductQuery query) {
        if (query.nome() != null && !query.nome().isBlank()) {
            q.setParameter("nome", "%" + query.nome().toLowerCase() + "%");
        }
        if (query.categoriaId() != null) {
            q.setParameter("categoriaId", query.categoriaId());
        }
        if (query.status() != null && !query.status().isBlank()) {
            q.setParameter("status", query.status().toUpperCase());
        }
        if (query.destaque() != null) {
            q.setParameter("destaque", query.destaque());
        }
    }

    private String resolveAdminOrderBy(String sort) {
        return switch (sort == null ? "mais_recente" : sort) {
            case "nome_asc"  -> "p.name ASC";
            case "nome_desc" -> "p.name DESC";
            case "status"    -> "p.status ASC, p.created_at DESC";
            case "destaque"  -> "p.featured DESC, p.created_at DESC";
            default          -> "p.created_at DESC";
        };
    }

    private ListAdminProductsResult.AdminProductItem toAdminItem(Object[] row) {
        return new ListAdminProductsResult.AdminProductItem(
                (UUID)    row[0],
                (String)  row[1],
                (String)  row[2],
                (UUID)    row[3],
                (String)  row[4],
                (String)  row[5],
                (Boolean) row[6],
                ((Number) row[8]).intValue(),
                ((Number) row[9]).intValue(),
                ((Number) row[10]).intValue(),
                toInstant(row[7])
        );
    }

    // ── Admin detail (by id) ──────────────────────────────────────────────────

    @Override
    public Optional<GetAdminProductDetailResult> findByIdForAdmin(UUID id) {
        var productOpt = productJpaRepository.findById(id);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        var product = productOpt.get();

        String categoryName = categoryJpaRepository.findById(product.getCategoryId())
                .map(CategoryEntity::getName)
                .orElse("");

        var images = productImageJpaRepository
                .findAllByProductIdOrderByImageOrderAsc(product.getId())
                .stream()
                .map(img -> new GetAdminProductDetailResult.ImageItem(
                        img.getId(), img.getUrl(), img.isPrincipal(), img.getImageOrder()))
                .toList();

        var variants = product.getVariants().stream()
                .map(v -> new GetAdminProductDetailResult.VariantItem(
                        v.getId(), v.getAttributeName(), v.getAttributeValue(),
                        v.getPrice(), v.getStock(), v.getSku(), v.isActive(), v.getCreatedAt()))
                .toList();

        return Optional.of(new GetAdminProductDetailResult(
                product.getId(), product.getName(), product.getSlug(),
                product.getDescription(), product.getCategoryId(), categoryName,
                product.getStatus().name(), product.isFeatured(), product.getCreatedAt(),
                images, variants));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Instant toInstant(Object obj) {
        return switch (obj) {
            case Timestamp ts          -> ts.toInstant();
            case OffsetDateTime odt    -> odt.toInstant();
            case Instant i             -> i;
            case null                  -> null;
            default -> throw new IllegalStateException("Unexpected timestamp type: " + obj.getClass());
        };
    }
}
