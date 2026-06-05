package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailResult;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersResult;
import br.com.miriageekstore.order.domain.port.in.OrderSummaryResult;
import org.springframework.stereotype.Component;

@Component
class AdminOrderWebMapper {

    AdminOrderPageResponse toPageResponse(ListAdminOrdersResult result) {
        var content = result.content().stream().map(this::toSummaryResponse).toList();
        return new AdminOrderPageResponse(content, result.page(), result.size(),
                result.totalElements(), result.totalPages());
    }

    private AdminOrderSummaryResponse toSummaryResponse(ListAdminOrdersResult.AdminOrderItem item) {
        return new AdminOrderSummaryResponse(
                item.id(),
                item.orderNumber(),
                item.status(),
                item.customerName(),
                item.customerEmail(),
                item.totalItems(),
                item.total(),
                item.createdAt(),
                item.updatedAt()
        );
    }

    OrderSummaryResponse toSummaryResponse(OrderSummaryResult result) {
        var period = new OrderSummaryResponse.Period(
                result.period().startDate(), result.period().endDate());
        var totals = new OrderSummaryResponse.Totals(
                result.totals().totalOrders(),
                result.totals().totalRevenue(),
                result.totals().averageTicket());
        var byStatus = result.byStatus().stream()
                .map(s -> new OrderSummaryResponse.StatusSummary(
                        s.status(), s.count(), s.percentage(), s.totalValue()))
                .toList();
        return new OrderSummaryResponse(period, totals, byStatus);
    }

    AdminOrderDetailResponse toDetailResponse(GetAdminOrderDetailResult result) {
        var customer = new AdminOrderDetailResponse.CustomerInfo(
                result.customer().id(),
                result.customer().name(),
                result.customer().email()
        );

        var items = result.items().stream().map(i -> new AdminOrderDetailResponse.OrderItemInfo(
                i.productName(), i.attributeName(), i.attributeValue(),
                i.sku(), i.principalImage(), i.unitPrice(), i.quantity(), i.subtotal()
        )).toList();

        AdminOrderDetailResponse.DeliveryAddressInfo address = result.deliveryAddress() == null ? null :
                new AdminOrderDetailResponse.DeliveryAddressInfo(
                        result.deliveryAddress().alias(),
                        result.deliveryAddress().street(),
                        result.deliveryAddress().number(),
                        result.deliveryAddress().complement(),
                        result.deliveryAddress().neighborhood(),
                        result.deliveryAddress().city(),
                        result.deliveryAddress().state(),
                        result.deliveryAddress().zipCode()
                );

        AdminOrderDetailResponse.PaymentInfo payment = result.payment() == null ? null :
                new AdminOrderDetailResponse.PaymentInfo(
                        result.payment().id(),
                        result.payment().status(),
                        result.payment().paymentMethod(),
                        result.payment().amount(),
                        result.payment().paymentDate(),
                        result.payment().gateway()
                );

        var history = result.statusHistory().stream().map(h -> new AdminOrderDetailResponse.StatusHistoryEntry(
                h.status(), h.note(), h.adminId(), h.adminName(), h.changedAt()
        )).toList();

        return new AdminOrderDetailResponse(
                result.id(), result.orderNumber(), result.status(),
                result.total(), result.createdAt(), result.updatedAt(),
                customer, items, address, payment, history
        );
    }
}
