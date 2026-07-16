package com.snapBuy.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.snapBuy.admin.dto.request.CreateMerchantRequest;
import com.snapBuy.admin.dto.request.UpdateMerchantRequest;
import com.snapBuy.admin.dto.response.MerchantResponse;

public interface AdminMerchantService {

    MerchantResponse createMerchant(CreateMerchantRequest request);

    MerchantResponse updateMerchant(Long merchantId, UpdateMerchantRequest request);

    void deleteMerchant(Long merchantId);

    MerchantResponse blockMerchant(Long merchantId);

    MerchantResponse unblockMerchant(Long merchantId);

    Page<MerchantResponse> listMerchants(String keyword, Boolean locked, Pageable pageable);

    MerchantResponse getMerchant(Long merchantId);
}