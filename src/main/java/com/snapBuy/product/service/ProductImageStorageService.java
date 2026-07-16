package com.snapBuy.product.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Stores product images on local disk under uploads/products/{productId}/.
 * Scoped to products only, since that's the only place image upload is
 * needed right now - no generic FileStorageService abstraction.
 */
@Slf4j
@Service
public class ProductImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String store(MultipartFile file, Long productId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File exceeds the 5MB size limit");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG, PNG, and WEBP images are allowed");
        }

        try {
            Path targetDir = Paths.get(uploadDir, "products", String.valueOf(productId));
            Files.createDirectories(targetDir);

            String filename = UUID.randomUUID() + "." + extension;
            Path targetFile = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetFile);

            return "/uploads/products/" + productId + "/" + filename;
        } catch (IOException ex) {
            log.error("Failed to store product image: {}", ex.getMessage());
            throw new RuntimeException("Failed to store uploaded image", ex);
        }
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            Path path = Paths.get(uploadDir, imageUrl.substring("/uploads/".length()));
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            log.warn("Failed to delete image {}: {}", imageUrl, ex.getMessage());
        }
    }
}