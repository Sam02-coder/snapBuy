package com.snapBuy.customer.spec;

import org.springframework.data.jpa.domain.Specification;

import com.snapBuy.customer.entity.CustomerProfile;

public final class CustomerProfileSpecifications {

    private CustomerProfileSpecifications() {
    }

    public static Specification<CustomerProfile> nameOrEmailContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("user").get("email")), pattern));
        };
    }

    public static Specification<CustomerProfile> isLocked(Boolean locked) {
        return (root, query, cb) -> locked == null ? null : cb.equal(root.get("user").get("locked"), locked);
    }
}