package com.snapBuy.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.product.entity.Product;

import org.springframework.data.jpa.repository.EntityGraph;

public interface ProductRepository extends JpaRepository<Product, Long>,
JpaSpecificationExecutor<Product> {

	@Override
	@EntityGraph(attributePaths = {
	    "category",
	    "images",
	    "merchant"
	})
	Optional<Product> findById(Long id);
	
	Page<Product> findByMerchantId(Long merchantId, Pageable pageable);
	
	Page<Product> findByApprovalStatus(ApprovalStatus status, Pageable pageable);
	
	Page<Product> findByCategoryIdAndApprovalStatusAndActiveTrue(
	    Long categoryId,
	    ApprovalStatus status,
	    Pageable pageable);
}