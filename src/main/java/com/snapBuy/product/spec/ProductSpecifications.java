package com.snapBuy.product.spec;

import org.springframework.data.jpa.domain.Specification;

import com.snapBuy.common.enums.ApprovalStatus;
import com.snapBuy.product.entity.Product;

import java.math.BigDecimal;

/**
 * Composable Specifications for the customer product browsing endpoint.
 * Combined with .and() in CustomerProductServiceImpl so any subset of
 * filters can be applied without a combinatorial explosion of repository methods.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    /** Every customer-facing query starts with this - never show pending/rejected/inactive/out-of-stock items. */
    public static Specification<Product> visibleToCustomers() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("approvalStatus"), ApprovalStatus.APPROVED),
                cb.isTrue(root.get("active")),
                cb.greaterThan(root.get("stock"), 0));
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> nameContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern));
        };
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal min) {
        return (root, query, cb) -> min == null ? null : cb.ge(root.get("price"), min);
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal max) {
        return (root, query, cb) -> max == null ? null : cb.le(root.get("price"), max);
    }

    public static Specification<Product> ownedByMerchant(Long merchantId) {
        return (root, query, cb) -> merchantId == null ? null : cb.equal(root.get("merchant").get("id"), merchantId);
    }
}