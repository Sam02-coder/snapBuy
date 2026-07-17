package com.snapBuy.admin.service.impl;

import com.snapBuy.admin.dto.response.CustomerResponse;
import com.snapBuy.admin.mapper.CustomerMapper;
import com.snapBuy.admin.service.AdminCustomerService;
import com.snapBuy.auth.repository.RefreshTokenRepository;
import com.snapBuy.customer.entity.CustomerProfile;
import com.snapBuy.customer.repository.CustomerProfileRepository;
import com.snapBuy.customer.spec.CustomerProfileSpecifications;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> listCustomers(String keyword,
                                                Boolean locked,
                                                Pageable pageable) {

        Specification<CustomerProfile> spec = Specification
                .where(CustomerProfileSpecifications.nameOrEmailContains(keyword))
                .and(CustomerProfileSpecifications.isLocked(locked));

        return customerProfileRepository
                .findAll(spec, pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        return customerMapper.toResponse(findProfileByUserId(customerId));
    }

    @Override
    @Transactional
    public CustomerResponse blockCustomer(Long customerId) {
        CustomerProfile profile = findProfileByUserId(customerId);
        User user = profile.getUser();
        user.setLocked(true);
        userRepository.save(user);
        refreshTokenRepository.revokeAllByUserId(customerId);
        return customerMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public CustomerResponse unblockCustomer(Long customerId) {
        CustomerProfile profile = findProfileByUserId(customerId);
        profile.getUser().setLocked(false);
        userRepository.save(profile.getUser());
        return customerMapper.toResponse(profile);
    }

    private CustomerProfile findProfileByUserId(Long userId) {
        return customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + userId));
    }
}