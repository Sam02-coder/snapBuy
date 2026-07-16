package com.snapBuy.merchant.service;

import com.snapBuy.customer.dto.request.ChangePasswordRequest;
import com.snapBuy.merchant.dto.request.FirstLoginPasswordChangeRequest;
import com.snapBuy.merchant.dto.request.UpdateMerchantProfileRequest;
import com.snapBuy.merchant.dto.response.MerchantProfileResponse;

public interface MerchantProfileService {

    void changeFirstLoginPassword(Long merchantId, FirstLoginPasswordChangeRequest request);

    void changePassword(Long merchantId, ChangePasswordRequest request);

    MerchantProfileResponse getProfile(Long merchantId);

    MerchantProfileResponse updateProfile(Long merchantId, UpdateMerchantProfileRequest request);
}