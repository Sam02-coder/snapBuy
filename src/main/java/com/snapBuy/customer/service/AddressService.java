package com.snapBuy.customer.service;

import java.util.List;

import com.snapBuy.customer.dto.request.AddressRequest;
import com.snapBuy.customer.dto.response.AddressResponse;

public interface AddressService {

    AddressResponse addAddress(Long customerId, AddressRequest request);

    AddressResponse updateAddress(Long customerId, Long addressId, AddressRequest request);

    void deleteAddress(Long customerId, Long addressId);

    List<AddressResponse> listAddresses(Long customerId);

    AddressResponse setDefault(Long customerId, Long addressId);
}