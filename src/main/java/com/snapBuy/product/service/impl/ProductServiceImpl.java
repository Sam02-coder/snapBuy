package com.snapBuy.product.service.impl;

import com.snapBuy.category.entity.Category;
import com.snapBuy.category.repository.CategoryRepository;
import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.exception.ForbiddenException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.product.dto.request.CreateProductRequest;
import com.snapBuy.product.dto.request.UpdateProductRequest;
import com.snapBuy.product.dto.request.UpdateStockRequest;
import com.snapBuy.product.dto.response.ProductResponse;
import com.snapBuy.product.entity.Product;
import com.snapBuy.product.entity.ProductImage;
import com.snapBuy.product.mapper.ProductMapper;
import com.snapBuy.product.repository.ProductImageRepository;
import com.snapBuy.product.repository.ProductRepository;
import com.snapBuy.product.service.CloudinaryService;
import com.snapBuy.product.service.ProductService;
import com.snapBuy.product.spec.ProductSpecifications;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(Long merchantId, CreateProductRequest request) {
        User merchant = userRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .merchant(merchant)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .approvalStatus(ApprovalStatus.PENDING)
                .active(true)
                .build();

        product = productRepository.save(product);
        log.info("Product created by merchant {}: {}", merchantId, product.getName());
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse updateProduct(Long merchantId, Long productId, UpdateProductRequest request) {
        Product product = findOwnedProduct(merchantId, productId);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);

        // Any content edit sends the product back through admin approval -
        // a merchant shouldn't be able to silently change an already-approved
        // listing's price/description without re-review.
        product.setApprovalStatus(ApprovalStatus.PENDING);
        product.setRejectionReason(null);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void deleteProduct(Long merchantId, Long productId) {
        Product product = findOwnedProduct(merchantId, productId);
        // Soft delete: cart_items/order_items reference products without cascade,
        // so a hard delete here would break FK integrity for anyone who already
        // has this product in a cart or past order. active=false hides it from
        // browsing/search while preserving order history integrity.
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse uploadImages(Long merchantId, Long productId, List<MultipartFile> files) {
        Product product = findOwnedProduct(merchantId, productId);

        int startOrder = product.getImages().size();
        for (int i = 0; i < files.size(); i++) {
        	String url = cloudinaryService.upload(files.get(i), productId);
        	ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .displayOrder(startOrder + i)
                    .build();
            productImageRepository.save(image);
            product.getImages().add(image);
        }

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse updateStock(Long merchantId, Long productId, UpdateStockRequest request) {
        Product product = findOwnedProduct(merchantId, productId);
        product.setStock(request.getStock());
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getMerchantProducts(
            Long merchantId,
            String keyword,
            Pageable pageable) {

        Specification<Product> spec = Specification
                .where(ProductSpecifications.ownedByMerchant(merchantId))
                .and(ProductSpecifications.nameContains(keyword));

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getMerchantProduct(Long merchantId, Long productId) {
        return productMapper.toResponse(findOwnedProduct(merchantId, productId));
    }

    private Product findOwnedProduct(Long merchantId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getMerchant().getId().equals(merchantId)) {
            throw new ForbiddenException("You do not have access to this product");
        }
        return product;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProductDetails(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        System.out.println("1. Product loaded");

        if (!product.isVisibleToCustomers()) {
            throw new ResourceNotFoundException("Product not found");
        }

        System.out.println("2. Before mapper");

        ProductResponse response = productMapper.toResponse(product);

        System.out.println("3. After mapper");

        return response;
    }
}