package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import br.com.miriageekstore.order.domain.model.Carrier;
import br.com.miriageekstore.order.domain.model.OrderStatus;
import br.com.miriageekstore.order.domain.port.in.AdminOrderQuery;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailUseCase;
import br.com.miriageekstore.order.domain.port.in.GetOrderSummaryUseCase;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersUseCase;
import br.com.miriageekstore.order.domain.port.in.OrderSummaryQuery;
import br.com.miriageekstore.order.domain.port.in.UpdateOrderStatusCommand;
import br.com.miriageekstore.order.domain.port.in.UpdateOrderStatusUseCase;
import br.com.miriageekstore.order.domain.port.in.UpdateTrackingCommand;
import br.com.miriageekstore.order.domain.port.in.UpdateTrackingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.Instant;

@Tag(name = "Admin Orders", description = "Order management — requires ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final ListAdminOrdersUseCase listAdminOrdersUseCase;
    private final GetAdminOrderDetailUseCase getAdminOrderDetailUseCase;
    private final GetOrderSummaryUseCase getOrderSummaryUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final UpdateTrackingUseCase updateTrackingUseCase;
    private final AdminOrderWebMapper mapper;

    @Operation(summary = "List orders with filters (admin)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping
    AdminOrderPageResponse listOrders(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) BigDecimal minValue,
            @RequestParam(required = false) BigDecimal maxValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        var query = new AdminOrderQuery(
                orderNumber, customerName, customerEmail, status,
                startDate, endDate, minValue, maxValue,
                Math.max(page, 0),
                Math.clamp(size, 1, 100),
                sort
        );
        return mapper.toPageResponse(listAdminOrdersUseCase.execute(query));
    }

    @Operation(summary = "Get order summary grouped by status (admin)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping("/summary")
    OrderSummaryResponse getOrderSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {

        return mapper.toSummaryResponse(
                getOrderSummaryUseCase.execute(new OrderSummaryQuery(startDate, endDate)));
    }

    @Operation(summary = "Get order detail by ID (admin)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping("/{id}")
    AdminOrderDetailResponse getOrderDetail(@PathVariable UUID id) {
        return mapper.toDetailResponse(getAdminOrderDetailUseCase.execute(id));
    }

    @Operation(summary = "Update order status (admin)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}/status")
    AdminOrderDetailResponse updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var adminId   = UUID.fromString(jwt.getSubject());
        var newStatus = OrderStatus.valueOf(request.status().toUpperCase());
        var command   = new UpdateOrderStatusCommand(id, newStatus, request.note(), adminId);

        updateOrderStatusUseCase.execute(command);

        return mapper.toDetailResponse(getAdminOrderDetailUseCase.execute(id));
    }

    @Operation(summary = "Add tracking code to shipped order (admin)",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PatchMapping("/{id}/tracking")
    TrackingResponse updateTracking(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTrackingRequest request) {

        var carrier = Carrier.valueOf(request.carrier().toUpperCase());
        var command = new UpdateTrackingCommand(
                id, request.trackingCode(), carrier,
                request.carrierName(), request.customTrackingUrl());

        var result = updateTrackingUseCase.execute(command);

        return new TrackingResponse(
                result.id(), result.orderNumber(), result.status(),
                result.trackingCode(), result.carrier(),
                result.carrierName(), result.trackingUrl());
    }
}
