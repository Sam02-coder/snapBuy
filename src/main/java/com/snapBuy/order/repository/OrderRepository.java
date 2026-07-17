package com.snapBuy.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.snapBuy.common.enums.OrderStatus;
import com.snapBuy.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.product.images",
            "address"
    })
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.product.images",
            "address"
    })
    java.util.Optional<Order> findById(Long id);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}