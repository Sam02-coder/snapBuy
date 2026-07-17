package com.snapBuy.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.category.dto.request.CreateCategoryRequest;
import com.snapBuy.category.dto.request.UpdateCategoryRequest;
import com.snapBuy.category.dto.response.CategoryResponse;
import com.snapBuy.category.entity.Category;
import com.snapBuy.category.mapper.CategoryMapper;
import com.snapBuy.category.repository.CategoryRepository;
import com.snapBuy.category.service.CategoryService;
import com.snapBuy.exception.DuplicateResourceException;
import com.snapBuy.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    @Caching(evict = {
    	    @CacheEvict(value = "categories", allEntries = true),
    	    @CacheEvict(value = "activeCategories", allEntries = true)
    	})
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("A category with this name already exists");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @Caching(evict = {
    	    @CacheEvict(value = "categories", allEntries = true),
    	    @CacheEvict(value = "activeCategories", allEntries = true)
    	})
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category = findCategory(categoryId);

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("A category with this name already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @Caching(evict = {
    	    @CacheEvict(value = "categories", allEntries = true),
    	    @CacheEvict(value = "activeCategories", allEntries = true)
    	})
    public void deleteCategory(Long categoryId) {
        Category category = findCategory(categoryId);
        // Soft delete: products reference categories without cascade, so a hard
        // delete would break FK integrity for existing products. Deactivating
        // hides the category from browsing/creation without touching history.
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Cacheable(value = "categories", key = "#categoryId")
    public CategoryResponse getCategory(Long categoryId) {
        return categoryMapper.toResponse(findCategory(categoryId));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    @Cacheable(
            value = "activeCategories",
            key = "'list'",
            sync = true
    )
    public List<CategoryResponse> getActiveCategories() {

        System.out.println("STEP-1");

        List<Category> categories = categoryRepository.findByActiveTrue();

        System.out.println("STEP-2 size = " + categories.size());

        List<CategoryResponse> response =
                categories.stream()
                        .map(categoryMapper::toResponse)
                        .collect(java.util.stream.Collectors.toList());

        System.out.println("STEP-3 mapped");

        return response;
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}