package com.snapBuy.category.service;


import java.util.List;

import com.snapBuy.category.dto.request.CreateCategoryRequest;
import com.snapBuy.category.dto.request.UpdateCategoryRequest;
import com.snapBuy.category.dto.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request);

    void deleteCategory(Long categoryId);

    CategoryResponse getCategory(Long categoryId);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getActiveCategories();
}