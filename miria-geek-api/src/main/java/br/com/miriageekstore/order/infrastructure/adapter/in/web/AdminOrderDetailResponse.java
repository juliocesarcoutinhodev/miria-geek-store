package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record AdminOrderDetailResponse(
        UUID id,
        String orderNumber,
        String status,
        BigDecimal total,
        Instant createdAt,
        Instant updatedAt,
        CustomerInfo customer,
        List<OrderItemInfo> items,
        DeliveryAddressInfo deliveryAddress,
        PaymentInfo payment,
        List<StatusHistoryEntry> statusHistory
) {
    record CustomerInfo(UUID id, String name, String email) {}

    record OrderItemInfo(
            String productName,
            String attributeName,
            String attributeValue,
            String sku,
            String principalImage,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal
    ) {}

    record DeliveryAddressInfo(
            String alias,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String zipCode
    ) {}

    record PaymentInfo(
            UUID id,
            String status,
            String paymentMethod,
            BigDecimal amount,
            Instant paymentDate,
            String gateway
    ) {}

    record StatusHistoryEntry(
            String status,
            String note,
            UUID adminId,
            String adminName,
            Instant changedAt
    ) {}
}
