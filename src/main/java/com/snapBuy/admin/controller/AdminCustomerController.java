package com.snapBuy.admin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.admin.dto.response.CustomerResponse;
import com.snapBuy.admin.service.AdminCustomerService;
import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;

@Tag(name = "Admin - Customers", description = "Admin management of customer accounts")
@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final AdminCustomerService adminCustomerService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> listCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        PageResponse<CustomerResponse> response =
                PageResponse.from(adminCustomerService.listCustomers(keyword, locked, pageable));
        return ResponseEntity.ok(ApiResponse.success("Customers fetched", response));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success("Customer fetched", adminCustomerService.getCustomer(customerId)));
    }

    @PatchMapping("/{customerId}/block")
    public ResponseEntity<ApiResponse<CustomerResponse>> blockCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success("Customer blocked", adminCustomerService.blockCustomer(customerId)));
    }

    @PatchMapping("/{customerId}/unblock")
    public ResponseEntity<ApiResponse<CustomerResponse>> unblockCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success("Customer unblocked", adminCustomerService.unblockCustomer(customerId)));
    }
}