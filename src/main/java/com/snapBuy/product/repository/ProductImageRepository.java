package com.snapBuy.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snapBuy.product.entity.ProductImage;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);
}