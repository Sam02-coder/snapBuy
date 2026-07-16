package com.snapBuy.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snapBuy.customer.entity.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    List<Address> findByUserIdAndIsDefaultTrue(Long userId);
}