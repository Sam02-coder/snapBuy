package com.snapBuy.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.snapBuy.common.enums.ApprovalStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long merchantId;
    private String merchantBusinessName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ApprovalStatus approvalStatus;
    private String rejectionReason;
    private boolean active;
    private List<ProductImageResponse> images;
    private LocalDateTime createdAt;
}