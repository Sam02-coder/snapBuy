package com.snapBuy.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.snapBuy.product.dto.request.CreateProductRequest;
import com.snapBuy.product.dto.request.UpdateProductRequest;
import com.snapBuy.product.dto.request.UpdateStockRequest;
import com.snapBuy.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(Long merchantId, CreateProductRequest request);

    ProductResponse updateProduct(Long merchantId, Long productId, UpdateProductRequest request);

    void deleteProduct(Long merchantId, Long productId);

    ProductResponse uploadImages(Long merchantId, Long productId, List<MultipartFile> files);

    ProductResponse updateStock(Long merchantId, Long productId, UpdateStockRequest request);

    Page<ProductResponse> getMerchantProducts(Long merchantId, String keyword, Pageable pageable);

    ProductResponse getMerchantProduct(Long merchantId, Long productId);
}