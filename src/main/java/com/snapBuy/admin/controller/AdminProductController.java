package com.snapBuy.admin.controller;

import com.snapBuy.admin.dto.request.RejectProductRequest;
import com.snapBuy.admin.service.AdminProductService;
import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import com.snapBuy.product.dto.response.ProductResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - Product Approval", description = "Admin review of merchant-submitted products")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ApprovalStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        PageResponse<ProductResponse> response =
                PageResponse.from(adminProductService.listProducts(keyword, status, pageable));
        return ResponseEntity.ok(ApiResponse.success("Products fetched", response));
    }

    @PatchMapping("/{productId}/approve")
    public ResponseEntity<ApiResponse<ProductResponse>> approveProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Product approved", adminProductService.approveProduct(productId)));
    }

    @PatchMapping("/{productId}/reject")
    public ResponseEntity<ApiResponse<ProductResponse>> rejectProduct(
            @PathVariable Long productId, @Valid @RequestBody RejectProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product rejected",
                adminProductService.rejectProduct(productId, request)));
    }
}