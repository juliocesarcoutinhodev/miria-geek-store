package br.com.miriageekstore.order.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.order.domain.model.Order;
import br.com.miriageekstore.order.domain.model.OrderId;
import br.com.miriageekstore.order.domain.model.OrderStatus;
import br.com.miriageekstore.order.domain.port.in.AdminOrderQuery;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailResult;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersResult;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import br.com.miriageekstore.order.domain.port.out.OrderWriteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class OrderPersistenceAdapter implements AdminOrderRepository, OrderWriteRepository {

    private final EntityManager em;

    @Override
    public ListAdminOrdersResult searchForAdmin(AdminOrderQuery query) {
        String conditions = buildConditions(query);

        String countSql =
                "SELECT COUNT(*)" +
                " FROM orders o" +
                " JOIN users u ON u.id = o.user_id" +
                " WHERE 1=1" + conditions;

        // col: 0=id, 1=order_number, 2=status, 3=customer_name, 4=customer_email,
        //      5=total_items, 6=total, 7=created_at, 8=updated_at
        String dataSql =
                "SELECT o.id, o.order_number, o.status," +
                " u.full_name, u.email," +
                " CAST(COALESCE(SUM(oi.quantity), 0) AS INTEGER)," +
                " o.total, o.created_at, o.updated_at" +
                " FROM orders o" +
                " JOIN users u ON u.id = o.user_id" +
                " LEFT JOIN order_items oi ON oi.order_id = o.id" +
                " WHERE 1=1" + conditions +
                " GROUP BY o.id, o.order_number, o.status, u.full_name, u.email, o.total, o.created_at, o.updated_at" +
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
        List<ListAdminOrdersResult.AdminOrderItem> content = rows.stream().map(this::toItem).toList();

        int totalPages = query.size() > 0 ? (int) Math.ceil((double) total / query.size()) : 0;
        return new ListAdminOrdersResult(content, query.page(), query.size(), total, totalPages);
    }

    private String buildConditions(AdminOrderQuery query) {
        var sb = new StringBuilder();
        if (query.orderNumber() != null && !query.orderNumber().isBlank()) {
            sb.append(" AND o.order_number = :orderNumber");
        }
        if (query.customerName() != null && !query.customerName().isBlank()) {
            sb.append(" AND LOWER(u.full_name) LIKE :customerName");
        }
        if (query.customerEmail() != null && !query.customerEmail().isBlank()) {
            sb.append(" AND LOWER(u.email) LIKE :customerEmail");
        }
        if (query.status() != null && !query.status().isBlank()) {
            sb.append(" AND o.status = :status");
        }
        if (query.startDate() != null) {
            sb.append(" AND o.created_at >= :startDate");
        }
        if (query.endDate() != null) {
            sb.append(" AND o.created_at <= :endDate");
        }
        if (query.minValue() != null) {
            sb.append(" AND o.total >= :minValue");
        }
        if (query.maxValue() != null) {
            sb.append(" AND o.total <= :maxValue");
        }
        return sb.toString();
    }

    private void applyParams(Query q, AdminOrderQuery query) {
        if (query.orderNumber() != null && !query.orderNumber().isBlank()) {
            q.setParameter("orderNumber", query.orderNumber().trim());
        }
        if (query.customerName() != null && !query.customerName().isBlank()) {
            q.setParameter("customerName", "%" + query.customerName().toLowerCase() + "%");
        }
        if (query.customerEmail() != null && !query.customerEmail().isBlank()) {
            q.setParameter("customerEmail", "%" + query.customerEmail().toLowerCase() + "%");
        }
        if (query.status() != null && !query.status().isBlank()) {
            q.setParameter("status", query.status().toUpperCase());
        }
        if (query.startDate() != null) {
            q.setParameter("startDate", query.startDate());
        }
        if (query.endDate() != null) {
            q.setParameter("endDate", query.endDate());
        }
        if (query.minValue() != null) {
            q.setParameter("minValue", query.minValue());
        }
        if (query.maxValue() != null) {
            q.setParameter("maxValue", query.maxValue());
        }
    }

    private String resolveOrderBy(String sort) {
        return switch (sort == null ? "latest" : sort) {
            case "oldest"     -> "o.created_at ASC";
            case "value_asc"  -> "o.total ASC";
            case "value_desc" -> "o.total DESC";
            case "status"     -> "o.status ASC, o.created_at DESC";
            default           -> "o.created_at DESC";
        };
    }

    private ListAdminOrdersResult.AdminOrderItem toItem(Object[] row) {
        return new ListAdminOrdersResult.AdminOrderItem(
                (UUID)       row[0],
                (String)     row[1],
                (String)     row[2],
                (String)     row[3],
                (String)     row[4],
                ((Number)    row[5]).intValue(),
                (BigDecimal) row[6],
                toInstant(row[7]),
                toInstant(row[8])
        );
    }

    // ── Detail ───────────────────────────────────────────────────────────────

    @Override
    public Optional<GetAdminOrderDetailResult> findByIdForAdmin(UUID id) {

        // 1. Order + customer + delivery address
        String orderSql =
                "SELECT o.id, o.order_number, o.status, o.total, o.created_at, o.updated_at," +
                " u.id, u.full_name, u.email," +
                " a.alias, a.street, a.number, a.complement, a.neighborhood, a.city, a.state, a.zip_code" +
                " FROM orders o" +
                " JOIN users u ON u.id = o.user_id" +
                " LEFT JOIN addresses a ON a.id = o.delivery_address_id" +
                " WHERE o.id = :id";

        @SuppressWarnings("unchecked")
        List<Object[]> orderRows = em.createNativeQuery(orderSql)
                .setParameter("id", id)
                .getResultList();

        if (orderRows.isEmpty()) return Optional.empty();

        Object[] o = orderRows.get(0);

        // 2. Items
        String itemsSql =
                "SELECT oi.product_name, oi.attribute_name, oi.attribute_value," +
                " oi.sku, oi.principal_image, oi.unit_price, oi.quantity, oi.subtotal" +
                " FROM order_items oi WHERE oi.order_id = :id";

        @SuppressWarnings("unchecked")
        List<Object[]> itemRows = em.createNativeQuery(itemsSql)
                .setParameter("id", id)
                .getResultList();

        // 3. Payment
        String paymentSql =
                "SELECT p.id, p.status, p.payment_method, p.amount, p.payment_date, p.gateway" +
                " FROM payments p WHERE p.order_id = :id LIMIT 1";

        @SuppressWarnings("unchecked")
        List<Object[]> paymentRows = em.createNativeQuery(paymentSql)
                .setParameter("id", id)
                .getResultList();

        // 4. Status history + admin name
        String historySql =
                "SELECT h.status, h.note, h.admin_id, u.full_name, h.changed_at" +
                " FROM order_status_history h" +
                " LEFT JOIN users u ON u.id = h.admin_id" +
                " WHERE h.order_id = :id ORDER BY h.changed_at ASC";

        @SuppressWarnings("unchecked")
        List<Object[]> historyRows = em.createNativeQuery(historySql)
                .setParameter("id", id)
                .getResultList();

        return Optional.of(buildDetailResult(o, itemRows, paymentRows, historyRows));
    }

    private GetAdminOrderDetailResult buildDetailResult(
            Object[] o,
            List<Object[]> itemRows,
            List<Object[]> paymentRows,
            List<Object[]> historyRows) {

        var customer = new GetAdminOrderDetailResult.CustomerInfo(
                (UUID)   o[6],
                (String) o[7],
                (String) o[8]
        );

        GetAdminOrderDetailResult.DeliveryAddressInfo address = o[9] == null ? null :
                new GetAdminOrderDetailResult.DeliveryAddressInfo(
                        (String) o[9],
                        (String) o[10],
                        (String) o[11],
                        (String) o[12],
                        (String) o[13],
                        (String) o[14],
                        (String) o[15],
                        (String) o[16]
                );

        var items = itemRows.stream().map(r -> new GetAdminOrderDetailResult.OrderItemInfo(
                (String)     r[0],
                (String)     r[1],
                (String)     r[2],
                (String)     r[3],
                (String)     r[4],
                (BigDecimal) r[5],
                ((Number)    r[6]).intValue(),
                (BigDecimal) r[7]
        )).toList();

        GetAdminOrderDetailResult.PaymentInfo payment = paymentRows.isEmpty() ? null :
                new GetAdminOrderDetailResult.PaymentInfo(
                        (UUID)       paymentRows.get(0)[0],
                        (String)     paymentRows.get(0)[1],
                        (String)     paymentRows.get(0)[2],
                        (BigDecimal) paymentRows.get(0)[3],
                        toInstant(paymentRows.get(0)[4]),
                        (String)     paymentRows.get(0)[5]
                );

        var history = historyRows.stream().map(r -> new GetAdminOrderDetailResult.StatusHistoryEntry(
                (String)  r[0],
                (String)  r[1],
                (UUID)    r[2],
                (String)  r[3],
                toInstant(r[4])
        )).toList();

        return new GetAdminOrderDetailResult(
                (UUID)       o[0],
                (String)     o[1],
                (String)     o[2],
                (BigDecimal) o[3],
                toInstant(o[4]),
                toInstant(o[5]),
                customer,
                items,
                address,
                payment,
                history
        );
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    public Optional<Order> findById(OrderId id) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                "SELECT o.id, o.status, o.user_id, o.created_at FROM orders o WHERE o.id = :id")
                .setParameter("id", id.value())
                .getResultList();

        if (rows.isEmpty()) return Optional.empty();

        Object[] row = rows.get(0);
        return Optional.of(Order.reconstitute(
                OrderId.of((UUID) row[0]),
                OrderStatus.valueOf((String) row[1]),
                (UUID) row[2],
                toInstant(row[3])
        ));
    }

    @Override
    public void updateStatus(OrderId id, OrderStatus status, Instant updatedAt) {
        em.createNativeQuery(
                "UPDATE orders SET status = :status, updated_at = :updatedAt WHERE id = :id")
                .setParameter("status", status.name())
                .setParameter("updatedAt", updatedAt)
                .setParameter("id", id.value())
                .executeUpdate();
    }

    @Override
    public void saveHistoryEntry(UUID id, OrderId orderId, OrderStatus status,
                                  String note, UUID adminId, Instant changedAt) {
        em.createNativeQuery(
                "INSERT INTO order_status_history (id, order_id, status, note, admin_id, changed_at)" +
                " VALUES (:id, :orderId, :status, :note, :adminId, :changedAt)")
                .setParameter("id", id)
                .setParameter("orderId", orderId.value())
                .setParameter("status", status.name())
                .setParameter("note", note)
                .setParameter("adminId", adminId)
                .setParameter("changedAt", changedAt)
                .executeUpdate();
    }

    @Override
    public void restoreStockForOrder(OrderId orderId) {
        em.createNativeQuery(
                "UPDATE product_variants pv" +
                " SET stock = pv.stock + oi.quantity" +
                " FROM order_items oi" +
                " WHERE oi.order_id = :orderId" +
                " AND pv.id = oi.variant_id" +
                " AND oi.variant_id IS NOT NULL")
                .setParameter("orderId", orderId.value())
                .executeUpdate();
    }

    private Instant toInstant(Object obj) {
        return switch (obj) {
            case Timestamp ts       -> ts.toInstant();
            case OffsetDateTime odt -> odt.toInstant();
            case Instant i          -> i;
            case null               -> null;
            default -> throw new IllegalStateException("Unexpected timestamp type: " + obj.getClass());
        };
    }
}
