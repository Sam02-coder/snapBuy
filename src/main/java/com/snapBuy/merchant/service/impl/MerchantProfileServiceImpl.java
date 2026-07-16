package com.snapBuy.merchant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.auth.repository.RefreshTokenRepository;
import com.snapBuy.customer.dto.request.ChangePasswordRequest;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.exception.UnauthorizedException;
import com.snapBuy.merchant.dto.request.FirstLoginPasswordChangeRequest;
import com.snapBuy.merchant.dto.request.UpdateMerchantProfileRequest;
import com.snapBuy.merchant.dto.response.MerchantProfileResponse;
import com.snapBuy.merchant.entity.MerchantProfile;
import com.snapBuy.merchant.mapper.MerchantProfileMapper;
import com.snapBuy.merchant.repository.MerchantProfileRepository;
import com.snapBuy.merchant.service.MerchantProfileService;
import com.snapBuy.notification.EmailService;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

@Service
@RequiredArgsConstructor
public class MerchantProfileServiceImpl implements MerchantProfileService {

    private final MerchantProfileRepository merchantProfileRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MerchantProfileMapper merchantProfileMapper;

    @Override
    @Transactional
    @CacheEvict(value = "merchantProfiles", key = "#merchantId")
    public void changeFirstLoginPassword(Long merchantId, FirstLoginPasswordChangeRequest request) {
        MerchantProfile profile = findProfile(merchantId);
        User user = profile.getUser();

        if (!passwordEncoder.matches(request.getTempPassword(), user.getPassword())) {
            throw new UnauthorizedException("Temporary password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        profile.setFirstLogin(false);
        merchantProfileRepository.save(profile);

        // Old tokens (issued against the temp-password session) should not outlive this change.
        refreshTokenRepository.revokeAllByUserId(merchantId);
        emailService.sendPasswordChangedEmail(user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(Long merchantId, ChangePasswordRequest request) {
        MerchantProfile profile = findProfile(merchantId);
        User user = profile.getUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(merchantId);
        emailService.sendPasswordChangedEmail(user.getEmail());
    }

    @Override
    @Cacheable(value = "merchantProfiles", key = "#merchantId")
    public MerchantProfileResponse getProfile(Long merchantId) {
        return merchantProfileMapper.toResponse(findProfile(merchantId));
    }

    @Override
    @Transactional
    @CacheEvict(value = "merchantProfiles", key = "#merchantId")
    public MerchantProfileResponse updateProfile(Long merchantId, UpdateMerchantProfileRequest request) {
        MerchantProfile profile = findProfile(merchantId);
        profile.setBusinessName(request.getBusinessName());
        profile.setGstNumber(request.getGstNumber());
        profile.setContactPhone(request.getContactPhone());
        return merchantProfileMapper.toResponse(merchantProfileRepository.save(profile));
    }

    private MerchantProfile findProfile(Long merchantId) {
        return merchantProfileRepository.findByUserId(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant profile not found"));
    }
}