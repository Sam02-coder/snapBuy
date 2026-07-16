package com.snapBuy.admin.service;

import com.snapBuy.admin.dto.request.RejectProductRequest;
import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.product.dto.response.ProductResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminProductService {

    Page<ProductResponse> listProducts(String keyword, ApprovalStatus status, Pageable pageable);

    ProductResponse approveProduct(Long productId);

    ProductResponse rejectProduct(Long productId, RejectProductRequest request);
}