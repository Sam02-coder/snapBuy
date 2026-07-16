package com.snapBuy.customer.service;

import com.snapBuy.customer.dto.request.ChangePasswordRequest;
import com.snapBuy.customer.dto.request.UpdateCustomerProfileRequest;
import com.snapBuy.customer.dto.response.CustomerProfileResponse;

public interface CustomerProfileService {

    CustomerProfileResponse getProfile(Long customerId);

    CustomerProfileResponse updateProfile(Long customerId, UpdateCustomerProfileRequest request);

    void changePassword(Long customerId, ChangePasswordRequest request);
}