package br.com.miriageekstore.order.domain.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PREPARING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
