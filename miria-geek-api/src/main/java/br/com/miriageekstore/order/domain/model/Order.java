package br.com.miriageekstore.order.domain.model;

import br.com.miriageekstore.order.domain.exception.InvalidOrderStatusTransitionException;
import br.com.miriageekstore.order.domain.exception.OrderNotShippedException;

import java.time.Instant;
import java.util.UUID;

public class Order {

    private final OrderId id;
    private OrderStatus status;
    private final UUID userId;
    private final String orderNumber;
    private final String customerEmail;
    private final String customerName;
    private final Instant createdAt;

    private Order(OrderId id, OrderStatus status, UUID userId,
                  String orderNumber, String customerEmail, String customerName,
                  Instant createdAt) {
        this.id = id;
        this.status = status;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.createdAt = createdAt;
    }

    public static Order reconstitute(OrderId id, OrderStatus status, UUID userId,
                                      String orderNumber, String customerEmail,
                                      String customerName, Instant createdAt) {
        return new Order(id, status, userId, orderNumber, customerEmail, customerName, createdAt);
    }

    public void transitionTo(OrderStatus newStatus) {
        if (!isValidTransition(this.status, newStatus)) {
            throw new InvalidOrderStatusTransitionException(this.status, newStatus);
        }
        this.status = newStatus;
    }

    public void requireShipped() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new OrderNotShippedException(this.status);
        }
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING_PAYMENT -> to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
            case PAID            -> to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED;
            case PREPARING       -> to == OrderStatus.SHIPPED;
            case SHIPPED         -> to == OrderStatus.DELIVERED;
            default              -> false;
        };
    }

    public OrderId getId()            { return id; }
    public OrderStatus getStatus()    { return status; }
    public UUID getUserId()           { return userId; }
    public String getOrderNumber()    { return orderNumber; }
    public String getCustomerEmail()  { return customerEmail; }
    public String getCustomerName()   { return customerName; }
    public Instant getCreatedAt()     { return createdAt; }
}
