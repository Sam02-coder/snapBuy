package com.snapBuy.admin.controller;

import com.snapBuy.admin.dto.request.CreateMerchantRequest;
import com.snapBuy.admin.dto.request.UpdateMerchantRequest;
import com.snapBuy.admin.dto.response.MerchantResponse;
import com.snapBuy.admin.service.AdminMerchantService;
import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(name = "Admin - Merchants", description = "Admin management of merchant accounts")
@RestController
@RequestMapping("/api/v1/admin/merchants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMerchantController {

    private final AdminMerchantService adminMerchantService;

    @PostMapping
    public ResponseEntity<ApiResponse<MerchantResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantRequest request) {
        MerchantResponse response = adminMerchantService.createMerchant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Merchant created. Credentials sent by email.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MerchantResponse>>> listMerchants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        PageResponse<MerchantResponse> response =
                PageResponse.from(adminMerchantService.listMerchants(keyword, locked, pageable));
        return ResponseEntity.ok(ApiResponse.success("Merchants fetched", response));
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<MerchantResponse>> getMerchant(@PathVariable Long merchantId) {
        return ResponseEntity.ok(ApiResponse.success("Merchant fetched", adminMerchantService.getMerchant(merchantId)));
    }

    @PutMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<MerchantResponse>> updateMerchant(
            @PathVariable Long merchantId, @Valid @RequestBody UpdateMerchantRequest request) {
        MerchantResponse response = adminMerchantService.updateMerchant(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("Merchant updated", response));
    }

    @DeleteMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<Void>> deleteMerchant(@PathVariable Long merchantId) {
        adminMerchantService.deleteMerchant(merchantId);
        return ResponseEntity.ok(ApiResponse.success("Merchant deleted"));
    }

    @PatchMapping("/{merchantId}/block")
    public ResponseEntity<ApiResponse<MerchantResponse>> blockMerchant(@PathVariable Long merchantId) {
        return ResponseEntity.ok(ApiResponse.success("Merchant blocked", adminMerchantService.blockMerchant(merchantId)));
    }

    @PatchMapping("/{merchantId}/unblock")
    public ResponseEntity<ApiResponse<MerchantResponse>> unblockMerchant(@PathVariable Long merchantId) {
        return ResponseEntity.ok(ApiResponse.success("Merchant unblocked", adminMerchantService.unblockMerchant(merchantId)));
    }
}