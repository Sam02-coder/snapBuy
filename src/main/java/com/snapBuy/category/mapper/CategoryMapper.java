package com.snapBuy.category.mapper;

import org.mapstruct.Mapper;

import com.snapBuy.category.dto.response.CategoryResponse;
import com.snapBuy.category.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
}