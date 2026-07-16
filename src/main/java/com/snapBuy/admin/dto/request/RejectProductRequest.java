package com.snapBuy.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectProductRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;
}