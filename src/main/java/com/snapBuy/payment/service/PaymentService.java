package com.snapBuy.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.snapBuy.payment.dto.request.VerifyPaymentRequest;
import com.snapBuy.payment.dto.response.CreateRazorpayOrderResponse;
import com.snapBuy.payment.dto.response.PaymentResponse;

public interface PaymentService {

    CreateRazorpayOrderResponse createRazorpayOrder(Long customerId, Long orderId);

    PaymentResponse verifyPayment(Long customerId, VerifyPaymentRequest request);

    Page<PaymentResponse> getPaymentHistory(Long customerId, Pageable pageable);
}