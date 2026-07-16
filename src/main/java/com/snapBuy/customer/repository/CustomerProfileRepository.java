package com.snapBuy.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.snapBuy.customer.entity.CustomerProfile;

import java.util.Optional;

public interface CustomerProfileRepository
        extends JpaRepository<CustomerProfile, Long>, JpaSpecificationExecutor<CustomerProfile> {

    Optional<CustomerProfile> findByUserId(Long userId);
}