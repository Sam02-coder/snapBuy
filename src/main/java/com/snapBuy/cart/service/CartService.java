package com.snapBuy.cart.service;

import com.snapBuy.cart.dto.request.AddToCartRequest;
import com.snapBuy.cart.dto.request.UpdateCartItemRequest;
import com.snapBuy.cart.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long customerId);

    CartResponse addToCart(Long customerId, AddToCartRequest request);

    CartResponse updateQuantity(Long customerId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long customerId, Long itemId);

    void clearCart(Long customerId);
}