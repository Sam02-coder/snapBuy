package com.snapBuy.product.controller;

import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import com.snapBuy.product.dto.request.CreateProductRequest;
import com.snapBuy.product.dto.request.UpdateProductRequest;
import com.snapBuy.product.dto.request.UpdateStockRequest;
import com.snapBuy.product.dto.response.ProductResponse;
import com.snapBuy.product.service.ProductService;
import com.snapBuy.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Merchant - Products", description = "Merchant product CRUD, images, and inventory")
@RestController
@RequestMapping("/api/v1/merchant/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created and pending admin approval", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> listMyProducts(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        PageResponse<ProductResponse> response =
                PageResponse.from(productService.getMerchantProducts(principal.getId(), keyword, pageable));
        return ResponseEntity.ok(ApiResponse.success("Products fetched", response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched",
                productService.getMerchantProduct(principal.getId(), productId)));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(principal.getId(), productId, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated, pending re-approval", response));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long productId) {
        productService.deleteProduct(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from listing"));
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadImages(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) {
        ProductResponse response = productService.uploadImages(principal.getId(), productId, files);
        return ResponseEntity.ok(ApiResponse.success("Images uploaded", response));
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockRequest request) {
        ProductResponse response = productService.updateStock(principal.getId(), productId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock updated", response));
    }
}