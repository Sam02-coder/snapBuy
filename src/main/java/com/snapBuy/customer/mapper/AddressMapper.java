package com.snapBuy.customer.mapper;

import org.mapstruct.Mapper;

import com.snapBuy.customer.dto.response.AddressResponse;
import com.snapBuy.customer.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(Address address);
}