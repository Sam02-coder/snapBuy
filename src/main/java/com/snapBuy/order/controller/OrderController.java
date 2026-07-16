package com.snapBuy.order.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import com.snapBuy.order.dto.request.CheckoutRequest;
import com.snapBuy.order.dto.response.OrderResponse;
import com.snapBuy.order.service.OrderService;
import com.snapBuy.security.CustomUserDetails;

@Tag(name = "Customer - Orders", description = "Checkout, order history, details, and cancellation")
@RestController
@RequestMapping("/api/v1/customer/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody CheckoutRequest request) {
        OrderResponse response = orderService.checkout(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order placed", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrderHistory(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        PageResponse<OrderResponse> response =
                PageResponse.from(orderService.getOrderHistory(principal.getId(), pageable));
        return ResponseEntity.ok(ApiResponse.success("Order history fetched", response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched",
                orderService.getOrderDetails(principal.getId(), orderId)));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled",
                orderService.cancelOrder(principal.getId(), orderId)));
    }
}