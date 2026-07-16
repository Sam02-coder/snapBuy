package com.snapBuy.merchant.entity;

import com.snapBuy.common.entity.BaseEntity;
import com.snapBuy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "merchant_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MerchantProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 150)
    private String businessName;

    private String gstNumber;

    private String contactPhone;

    /**
     * True until the merchant changes their temp password on first login.
     * The JWT filter blocks every endpoint except change-password while this is true.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean firstLogin = true;
}