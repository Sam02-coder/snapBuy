package com.snapBuy.product.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import com.snapBuy.product.dto.response.ProductResponse;
import com.snapBuy.product.service.CustomerProductService;

import java.math.BigDecimal;

@Tag(name = "Products", description = "Public product browsing, search, filter, and sort")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> browseProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE), Sort.by(direction, sortBy));

        PageResponse<ProductResponse> response = PageResponse.from(
                customerProductService.browseProducts(keyword, categoryId, minPrice, maxPrice, pageable));
        return ResponseEntity.ok(ApiResponse.success("Products fetched", response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetails(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched",
                customerProductService.getProductDetails(productId)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> browseByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, AppConstants.DEFAULT_SORT_BY));
        PageResponse<ProductResponse> response = PageResponse.from(
                customerProductService.browseProducts(null, categoryId, null, null, pageable));
        return ResponseEntity.ok(ApiResponse.success("Products fetched", response));
    }
}