package com.snapBuy.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;
    private int availableStock;

    /** True if quantity in cart exceeds current stock - frontend should warn before checkout. */
    private boolean exceedsAvailableStock;
}