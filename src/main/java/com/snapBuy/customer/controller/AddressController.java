package com.snapBuy.customer.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.customer.dto.request.AddressRequest;
import com.snapBuy.customer.dto.response.AddressResponse;
import com.snapBuy.customer.service.AddressService;
import com.snapBuy.security.CustomUserDetails;

import java.util.List;

@Tag(name = "Customer - Addresses", description = "Customer address book management")
@RestController
@RequestMapping("/api/v1/customer/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.addAddress(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Address added", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> listAddresses(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched", addressService.listAddresses(principal.getId())));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.updateAddress(principal.getId(), addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated", response));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long addressId) {
        addressService.deleteAddress(principal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted"));
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long addressId) {
        return ResponseEntity.ok(ApiResponse.success("Default address updated",
                addressService.setDefault(principal.getId(), addressId)));
    }
}