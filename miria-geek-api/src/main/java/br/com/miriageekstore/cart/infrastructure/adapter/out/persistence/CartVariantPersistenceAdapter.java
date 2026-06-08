package br.com.miriageekstore.cart.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.cart.domain.port.out.CartVariantRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class CartVariantPersistenceAdapter implements CartVariantRepository {

    private final EntityManager em;

    private static final String SELECT_VARIANT =
            "SELECT pv.id, p.id, p.name, pv.attribute_name, pv.attribute_value, pv.sku," +
            " (SELECT pi.url FROM product_images pi" +
            "  WHERE pi.product_id = p.id AND pi.principal = true LIMIT 1)," +
            " pv.price, pv.stock, pv.active, p.status," +
            " CASE WHEN pv.weight IS NOT NULL AND pv.width IS NOT NULL" +
            "           AND pv.height IS NOT NULL AND pv.depth IS NOT NULL" +
            "      THEN true ELSE false END," +
            " pv.weight, pv.width, pv.height, pv.depth" +
            " FROM product_variants pv" +
            " JOIN products p ON pv.product_id = p.id";

    @Override
    public Optional<CartVariantView> findById(UUID variantId) {
        var query = em.createNativeQuery(SELECT_VARIANT + " WHERE pv.id = :variantId");
        query.setParameter("variantId", variantId);
        try {
            return Optional.of(mapRow((Object[]) query.getSingleResult()));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CartVariantView> findAllByIds(List<UUID> variantIds) {
        if (variantIds.isEmpty()) return List.of();
        var query = em.createNativeQuery(SELECT_VARIANT + " WHERE pv.id IN (:variantIds)");
        query.setParameter("variantIds", variantIds);
        return ((List<Object[]>) query.getResultList()).stream().map(this::mapRow).toList();
    }

    private CartVariantView mapRow(Object[] row) {
        return new CartVariantView(
                (UUID)       row[0],
                (UUID)       row[1],
                (String)     row[2],
                (String)     row[3],
                (String)     row[4],
                (String)     row[5],
                (String)     row[6],
                (BigDecimal) row[7],
                ((Number)    row[8]).intValue(),
                (Boolean)    row[9],
                "ACTIVE".equals(row[10]),
                (Boolean)    row[11],
                (BigDecimal) row[12],
                (BigDecimal) row[13],
                (BigDecimal) row[14],
                (BigDecimal) row[15]
        );
    }
}
