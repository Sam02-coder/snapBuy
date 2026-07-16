package com.snapBuy.merchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.snapBuy.merchant.entity.MerchantProfile;

import java.util.Optional;

public interface MerchantProfileRepository
        extends JpaRepository<MerchantProfile, Long>, JpaSpecificationExecutor<MerchantProfile> {

    Optional<MerchantProfile> findByUserId(Long userId);
}