package com.snapBuy.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.snapBuy.product.dto.response.ProductImageResponse;
import com.snapBuy.product.dto.response.ProductResponse;
import com.snapBuy.product.entity.Product;
import com.snapBuy.product.entity.ProductImage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "merchant.id", target = "merchantId")
    @Mapping(target = "merchantBusinessName", ignore = true)
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    ProductImageResponse toImageResponse(ProductImage image);
}