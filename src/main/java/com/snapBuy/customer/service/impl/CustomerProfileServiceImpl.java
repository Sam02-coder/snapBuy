package com.snapBuy.customer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.auth.repository.RefreshTokenRepository;
import com.snapBuy.customer.dto.request.ChangePasswordRequest;
import com.snapBuy.customer.dto.request.UpdateCustomerProfileRequest;
import com.snapBuy.customer.dto.response.CustomerProfileResponse;
import com.snapBuy.customer.entity.CustomerProfile;
import com.snapBuy.customer.mapper.CustomerProfileMapper;
import com.snapBuy.customer.repository.CustomerProfileRepository;
import com.snapBuy.customer.service.CustomerProfileService;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.exception.UnauthorizedException;
import com.snapBuy.notification.EmailService;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CustomerProfileMapper customerProfileMapper;

    @Override
    public CustomerProfileResponse getProfile(Long customerId) {
        return customerProfileMapper.toResponse(findProfile(customerId));
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfile(Long customerId, UpdateCustomerProfileRequest request) {
        CustomerProfile profile = findProfile(customerId);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        return customerProfileMapper.toResponse(customerProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public void changePassword(Long customerId, ChangePasswordRequest request) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(customerId);
        emailService.sendPasswordChangedEmail(user.getEmail());
    }

    private CustomerProfile findProfile(Long customerId) {
        return customerProfileRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
    }
}