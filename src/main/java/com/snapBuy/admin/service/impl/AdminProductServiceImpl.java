package com.snapBuy.admin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.admin.dto.request.RejectProductRequest;
import com.snapBuy.admin.service.AdminProductService;
import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.product.dto.response.ProductResponse;
import com.snapBuy.product.entity.Product;
import com.snapBuy.product.mapper.ProductMapper;
import com.snapBuy.product.repository.ProductRepository;
import com.snapBuy.product.spec.ProductSpecifications;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> listProducts(String keyword, ApprovalStatus status, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.nameContains(keyword))
                .and(status != null
                        ? (root, query, cb) -> cb.equal(root.get("approvalStatus"), status)
                        : null);
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse approveProduct(Long productId) {
        Product product = findProduct(productId);
        product.setApprovalStatus(ApprovalStatus.APPROVED);
        product.setRejectionReason(null);
        log.info("Product approved by admin: id={}", productId);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse rejectProduct(Long productId, RejectProductRequest request) {
        Product product = findProduct(productId);
        product.setApprovalStatus(ApprovalStatus.REJECTED);
        product.setRejectionReason(request.getReason());
        log.info("Product rejected by admin: id={}, reason={}", productId, request.getReason());
        return productMapper.toResponse(productRepository.save(product));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}