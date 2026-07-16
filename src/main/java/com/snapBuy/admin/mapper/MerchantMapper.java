package com.snapBuy.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.snapBuy.admin.dto.response.MerchantResponse;
import com.snapBuy.merchant.entity.MerchantProfile;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.active", target = "active")
    @Mapping(source = "user.locked", target = "locked")
    @Mapping(source = "user.createdAt", target = "createdAt")
    MerchantResponse toResponse(MerchantProfile profile);
}