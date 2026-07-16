package com.snapBuy.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMerchantRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String gstNumber;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
}