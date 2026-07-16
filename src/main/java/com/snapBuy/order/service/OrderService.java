package com.snapBuy.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.snapBuy.order.dto.request.CheckoutRequest;
import com.snapBuy.order.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse checkout(Long customerId, CheckoutRequest request);

    Page<OrderResponse> getOrderHistory(Long customerId, Pageable pageable);

    OrderResponse getOrderDetails(Long customerId, Long orderId);

    OrderResponse cancelOrder(Long customerId, Long orderId);
}