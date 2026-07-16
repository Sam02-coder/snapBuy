package com.snapBuy.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.snapBuy.admin.dto.response.CustomerResponse;
import com.snapBuy.customer.entity.CustomerProfile;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.active", target = "active")
    @Mapping(source = "user.locked", target = "locked")
    @Mapping(source = "user.createdAt", target = "createdAt")
    CustomerResponse toResponse(CustomerProfile profile);
}