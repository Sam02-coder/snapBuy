package com.snapBuy.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snapBuy.cart.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);
}