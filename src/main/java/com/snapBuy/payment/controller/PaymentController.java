package com.snapBuy.payment.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import com.snapBuy.payment.dto.request.VerifyPaymentRequest;
import com.snapBuy.payment.dto.response.CreateRazorpayOrderResponse;
import com.snapBuy.payment.dto.response.PaymentResponse;
import com.snapBuy.payment.service.PaymentService;
import com.snapBuy.security.CustomUserDetails;

@Tag(name = "Customer - Payments", description = "Razorpay order creation, verification, and payment history")
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}/payment/create")
    public ResponseEntity<ApiResponse<CreateRazorpayOrderResponse>> createRazorpayOrder(
            @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long orderId) {
        CreateRazorpayOrderResponse response = paymentService.createRazorpayOrder(principal.getId(), orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment initiated", response));
    }

    @PostMapping("/payments/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody VerifyPaymentRequest request) {
        PaymentResponse response = paymentService.verifyPayment(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified. Order confirmed.", response));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPaymentHistory(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        PageResponse<PaymentResponse> response =
                PageResponse.from(paymentService.getPaymentHistory(principal.getId(), pageable));
        return ResponseEntity.ok(ApiResponse.success("Payment history fetched", response));
    }
}