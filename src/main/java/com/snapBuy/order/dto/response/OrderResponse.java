package com.snapBuy.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.snapBuy.common.enums.OrderStatus;
import com.snapBuy.customer.dto.response.AddressResponse;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private AddressResponse deliveryAddress;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}