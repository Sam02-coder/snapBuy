package com.snapBuy.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMerchantProfileRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String gstNumber;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
}