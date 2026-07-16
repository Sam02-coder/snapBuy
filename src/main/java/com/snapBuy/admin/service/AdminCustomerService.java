package com.snapBuy.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.snapBuy.admin.dto.response.CustomerResponse;

public interface AdminCustomerService {

    Page<CustomerResponse> listCustomers(String keyword, Boolean locked, Pageable pageable);

    CustomerResponse getCustomer(Long customerId);

    CustomerResponse blockCustomer(Long customerId);

    CustomerResponse unblockCustomer(Long customerId);
}