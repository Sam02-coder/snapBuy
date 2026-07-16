package com.snapBuy.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);

    Page<Product> findByApprovalStatus(ApprovalStatus status, Pageable pageable);

    Page<Product> findByCategoryIdAndApprovalStatusAndActiveTrue(
            Long categoryId, ApprovalStatus status, Pageable pageable);
}