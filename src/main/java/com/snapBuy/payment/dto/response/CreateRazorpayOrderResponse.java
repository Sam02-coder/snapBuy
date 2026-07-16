package com.snapBuy.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRazorpayOrderResponse {
    private String razorpayOrderId;
    private long amountInPaise;
    private String currency;
    private String razorpayKeyId;
    private Long internalOrderId;
}