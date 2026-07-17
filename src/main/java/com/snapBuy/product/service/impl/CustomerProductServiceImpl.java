package com.snapBuy.product.service.impl;

import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.product.dto.response.ProductResponse;
import com.snapBuy.product.entity.Product;
import com.snapBuy.product.mapper.ProductMapper;
import com.snapBuy.product.repository.ProductRepository;
import com.snapBuy.product.service.CustomerProductService;
import com.snapBuy.product.spec.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerProductServiceImpl implements CustomerProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> browseProducts(
            String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        Specification<Product> spec = Specification
                .where(ProductSpecifications.visibleToCustomers())
                .and(ProductSpecifications.hasCategory(categoryId))
                .and(ProductSpecifications.nameContains(keyword))
                .and(ProductSpecifications.priceGreaterThanOrEqual(minPrice))
                .and(ProductSpecifications.priceLessThanOrEqual(maxPrice));

        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.isVisibleToCustomers()) {
            // Deliberately the same 404 as "doesn't exist" - a pending/rejected
            // product shouldn't be distinguishable from a nonexistent one to customers.
            throw new ResourceNotFoundException("Product not found");
        }

        return productMapper.toResponse(product);
    }
}