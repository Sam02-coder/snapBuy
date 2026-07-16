package com.snapBuy.merchant.spec;

import org.springframework.data.jpa.domain.Specification;

import com.snapBuy.merchant.entity.MerchantProfile;

public final class MerchantProfileSpecifications {

    private MerchantProfileSpecifications() {
    }

    public static Specification<MerchantProfile> businessNameOrEmailContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("businessName")), pattern),
                    cb.like(cb.lower(root.get("user").get("email")), pattern));
        };
    }

    public static Specification<MerchantProfile> isLocked(Boolean locked) {
        return (root, query, cb) -> locked == null ? null : cb.equal(root.get("user").get("locked"), locked);
    }
}