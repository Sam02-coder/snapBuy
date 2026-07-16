package com.snapBuy.merchant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.snapBuy.merchant.dto.response.MerchantProfileResponse;
import com.snapBuy.merchant.entity.MerchantProfile;

@Mapper(componentModel = "spring")
public interface MerchantProfileMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    MerchantProfileResponse toResponse(MerchantProfile profile);
}