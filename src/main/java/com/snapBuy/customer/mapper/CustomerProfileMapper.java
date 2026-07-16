package com.snapBuy.customer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.snapBuy.customer.dto.response.CustomerProfileResponse;
import com.snapBuy.customer.entity.CustomerProfile;

@Mapper(componentModel = "spring")
public interface CustomerProfileMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    CustomerProfileResponse toResponse(CustomerProfile profile);
}