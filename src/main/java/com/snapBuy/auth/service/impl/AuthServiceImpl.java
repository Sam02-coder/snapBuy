package com.snapBuy.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.auth.dto.request.ForgotPasswordRequest;
import com.snapBuy.auth.dto.request.LoginRequest;
import com.snapBuy.auth.dto.request.OtpVerifyRequest;
import com.snapBuy.auth.dto.request.RegisterRequest;
import com.snapBuy.auth.dto.request.ResendOtpRequest;
import com.snapBuy.auth.dto.request.ResetPasswordRequest;
import com.snapBuy.auth.dto.response.LoginResponse;
import com.snapBuy.auth.dto.response.TokenPairResponse;
import com.snapBuy.auth.entity.RefreshToken;
import com.snapBuy.auth.repository.RefreshTokenRepository;
import com.snapBuy.auth.service.AuthService;
import com.snapBuy.auth.service.OtpService;
import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.enums.Role;
import com.snapBuy.customer.entity.CustomerProfile;
import com.snapBuy.customer.repository.CustomerProfileRepository;
import com.snapBuy.exception.DuplicateResourceException;
import com.snapBuy.exception.InvalidOtpException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.exception.UnauthorizedException;
import com.snapBuy.merchant.entity.MerchantProfile;
import com.snapBuy.merchant.repository.MerchantProfileRepository;
import com.snapBuy.notification.EmailService;
import com.snapBuy.security.CustomUserDetails;
import com.snapBuy.security.JwtUtil;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .active(true)
                .locked(false)
                .emailVerified(false)
                .build();
        user = userRepository.save(user);

        CustomerProfile profile = CustomerProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .build();
        customerProfileRepository.save(profile);

        String otp = otpService.generateOtp(AppConstants.OTP_KEY_PREFIX, request.getEmail());
        emailService.sendOtpEmail(request.getEmail(), otp);

        log.info("Customer registered, pending OTP verification: {}", request.getEmail());
    }

    @Override
    @Transactional
    public void verifyOtp(OtpVerifyRequest request) {
        otpService.validateOtp(AppConstants.OTP_KEY_PREFIX, request.getEmail(), request.getOtp());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for: {}", request.getEmail());
    }

    @Override
    public void resendOtp(ResendOtpRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("No pending registration found for this email");
        }
        if (!otpService.canResend(AppConstants.OTP_KEY_PREFIX, request.getEmail())) {
            long wait = otpService.cooldownSecondsRemaining(AppConstants.OTP_KEY_PREFIX, request.getEmail());
            throw new InvalidOtpException("Please wait " + wait + " seconds before requesting another OTP");
        }

        String otp = otpService.generateOtp(AppConstants.OTP_KEY_PREFIX, request.getEmail());
        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String attemptsKey = AppConstants.LOGIN_ATTEMPT_PREFIX + request.getEmail();

        String currentAttempts = redisTemplate.opsForValue().get(attemptsKey);
        if (currentAttempts != null && Integer.parseInt(currentAttempts) >= AppConstants.MAX_LOGIN_ATTEMPTS) {
            throw new UnauthorizedException(
                    "Account temporarily locked due to too many failed attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
            if (attempts != null && attempts == 1) {
                redisTemplate.expire(attemptsKey, Duration.ofMinutes(AppConstants.LOGIN_LOCKOUT_MINUTES));
            }
            throw new UnauthorizedException("Invalid email or password");
        }

        redisTemplate.delete(attemptsKey);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        persistRefreshToken(user, refreshToken);

        boolean firstLogin = user.getRole() == Role.MERCHANT
                && merchantProfileRepository.findByUserId(user.getId())
                        .map(MerchantProfile::isFirstLogin)
                        .orElse(false);

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .firstLogin(firstLogin)
                .build();
    }

    @Override
    @Transactional
    public TokenPairResponse refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked. Please log in again.");
        }

        User user = storedToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Rotation: the old refresh token is single-use. Revoke it and issue a new pair.
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        persistRefreshToken(user, newRefreshToken);

        return TokenPairResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && !jwtUtil.isExpired(accessToken)) {
            long remainingMs = jwtUtil.getRemainingValidityMs(accessToken);
            // Blacklist for the remaining lifetime of the access token so it can't be reused.
            redisTemplate.opsForValue().set(
                    AppConstants.REFRESH_TOKEN_BLACKLIST_PREFIX + accessToken,
                    "1",
                    Duration.ofMillis(Math.max(remainingMs, 1000)));
        }

        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        // Deliberately silent on unknown email - prevents leaking which emails are registered.
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String otp = otpService.generateOtp(AppConstants.FORGOT_PASSWORD_OTP_PREFIX, request.getEmail());
            emailService.sendForgotPasswordOtpEmail(request.getEmail(), otp);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        otpService.validateOtp(AppConstants.FORGOT_PASSWORD_OTP_PREFIX, request.getEmail(), request.getOtp());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Force re-login on every device after a password reset.
        refreshTokenRepository.revokeAllByUserId(user.getId());

        emailService.sendPasswordChangedEmail(user.getEmail());
    }

    private void persistRefreshToken(User user, String token) {
        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpiryMs()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
    }
}