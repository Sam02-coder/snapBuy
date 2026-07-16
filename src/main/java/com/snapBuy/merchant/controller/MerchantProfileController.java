package com.snapBuy.merchant.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.customer.dto.request.ChangePasswordRequest;
import com.snapBuy.merchant.dto.request.FirstLoginPasswordChangeRequest;
import com.snapBuy.merchant.dto.request.UpdateMerchantProfileRequest;
import com.snapBuy.merchant.dto.response.MerchantProfileResponse;
import com.snapBuy.merchant.service.MerchantProfileService;
import com.snapBuy.security.CustomUserDetails;

@Tag(name = "Merchant - Profile", description = "Merchant self-service profile and password management")
@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantProfileController {

    private final MerchantProfileService merchantProfileService;

    @PostMapping("/first-login/change-password")
    public ResponseEntity<ApiResponse<Void>> changeFirstLoginPassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody FirstLoginPasswordChangeRequest request) {
        merchantProfileService.changeFirstLoginPassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed. You now have full access."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        merchantProfileService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", merchantProfileService.getProfile(principal.getId())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody UpdateMerchantProfileRequest request) {
        MerchantProfileResponse response = merchantProfileService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }
}