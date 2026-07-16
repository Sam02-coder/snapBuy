package com.snapBuy.admin.service.impl;

import com.snapBuy.admin.dto.request.CreateMerchantRequest;
import com.snapBuy.admin.dto.request.UpdateMerchantRequest;
import com.snapBuy.admin.dto.response.CustomerResponse;
import com.snapBuy.admin.dto.response.MerchantResponse;
import com.snapBuy.admin.mapper.CustomerMapper;
import com.snapBuy.admin.mapper.MerchantMapper;
import com.snapBuy.admin.service.AdminCustomerService;
import com.snapBuy.admin.service.AdminMerchantService;
import com.snapBuy.auth.repository.RefreshTokenRepository;
import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.enums.Role;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.common.response.PageResponse;
import com.snapBuy.common.util.PasswordGenerator;
import com.snapBuy.customer.entity.CustomerProfile;
import com.snapBuy.customer.repository.CustomerProfileRepository;
import com.snapBuy.customer.spec.CustomerProfileSpecifications;
import com.snapBuy.exception.DuplicateResourceException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.merchant.entity.MerchantProfile;
import com.snapBuy.merchant.repository.MerchantProfileRepository;
import com.snapBuy.merchant.spec.MerchantProfileSpecifications;
import com.snapBuy.notification.EmailService;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMerchantServiceImpl implements AdminMerchantService {

    private final UserRepository userRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantResponse createMerchant(CreateMerchantRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        String tempPassword = PasswordGenerator.generateTempPassword();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(Role.MERCHANT)
                .active(true)
                .locked(false)
                .emailVerified(true) // merchants skip OTP - admin already vetted them
                .build();
        user = userRepository.save(user);

        MerchantProfile profile = MerchantProfile.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .gstNumber(request.getGstNumber())
                .contactPhone(request.getContactPhone())
                .firstLogin(true)
                .build();
        profile = merchantProfileRepository.save(profile);

        emailService.sendMerchantCredentialsEmail(request.getEmail(), request.getBusinessName(), tempPassword);

        log.info("Merchant created by admin: {}", request.getEmail());
        return merchantMapper.toResponse(profile);
    }

    @Override
    @Transactional
    @CacheEvict(value = "merchantProfiles", key = "#merchantId")
    public MerchantResponse updateMerchant(Long merchantId, UpdateMerchantRequest request) {
        MerchantProfile profile = findProfileByUserId(merchantId);
        profile.setBusinessName(request.getBusinessName());
        profile.setGstNumber(request.getGstNumber());
        profile.setContactPhone(request.getContactPhone());
        return merchantMapper.toResponse(merchantProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public void deleteMerchant(Long merchantId) {
        MerchantProfile profile = findProfileByUserId(merchantId);
        // Note: once the product module (Day 7) is live, add a check here to
        // block deletion while the merchant has active products/orders -
        // reassign or soft-delete instead of a hard delete in that case.
        merchantProfileRepository.delete(profile);
        refreshTokenRepository.deleteByUserId(merchantId);
        userRepository.deleteById(merchantId);
        log.info("Merchant deleted by admin: userId={}", merchantId);
    }

    @Override
    @Transactional
    public MerchantResponse blockMerchant(Long merchantId) {
        MerchantProfile profile = findProfileByUserId(merchantId);
        User user = profile.getUser();
        user.setLocked(true);
        userRepository.save(user);
        refreshTokenRepository.revokeAllByUserId(merchantId); // force logout everywhere
        return merchantMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public MerchantResponse unblockMerchant(Long merchantId) {
        MerchantProfile profile = findProfileByUserId(merchantId);
        profile.getUser().setLocked(false);
        userRepository.save(profile.getUser());
        return merchantMapper.toResponse(profile);
    }

    @Override
    public Page<MerchantResponse> listMerchants(String keyword, Boolean locked, Pageable pageable) {
        Specification<MerchantProfile> spec = Specification
                .where(MerchantProfileSpecifications.businessNameOrEmailContains(keyword))
                .and(MerchantProfileSpecifications.isLocked(locked));
        return merchantProfileRepository.findAll(spec, pageable).map(merchantMapper::toResponse);
    }

    @Override
    public MerchantResponse getMerchant(Long merchantId) {
        return merchantMapper.toResponse(findProfileByUserId(merchantId));
    }

    private MerchantProfile findProfileByUserId(Long userId) {
        return merchantProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + userId));
    }
}