package com.snapBuy.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponse {
    private Long id;
    private String email;
    private String businessName;
    private String gstNumber;
    private String contactPhone;
    private boolean firstLogin;
    private boolean active;
    private boolean locked;
    private LocalDateTime createdAt;
}