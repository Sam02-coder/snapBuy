package com.snapBuy.category.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapBuy.category.dto.response.CategoryResponse;
import com.snapBuy.category.service.CategoryService;
import com.snapBuy.common.response.ApiResponse;

import java.util.List;

@Tag(name = "Categories", description = "Public category browsing")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listActiveCategories() {
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categoryService.getActiveCategories()));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched", categoryService.getCategory(categoryId)));
    }
}