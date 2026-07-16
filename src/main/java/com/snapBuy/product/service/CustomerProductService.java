package com.snapBuy.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.snapBuy.product.dto.response.ProductResponse;

import java.math.BigDecimal;

public interface CustomerProductService {

    Page<ProductResponse> browseProducts(
            String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    ProductResponse getProductDetails(Long productId);
}