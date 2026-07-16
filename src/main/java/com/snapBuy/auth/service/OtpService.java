package com.snapBuy.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.snapBuy.exception.InvalidOtpException;

import java.security.SecureRandom;
import java.time.Duration;

/**
 * Generic OTP engine shared by registration and forgot-password flows.
 * Each flow passes its own Redis key prefix so the two OTP spaces never collide.
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${app.otp.expiry-seconds}")
    private long otpExpirySeconds;

    @Value("${app.otp.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    @Value("${app.otp.max-attempts}")
    private int maxAttempts;

    /**
     * Generates a new 6-digit OTP, stores it, and resets the attempt counter.
     * Callers must check canResend() first to enforce the cooldown.
     */
    public String generateOtp(String prefix, String email) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));

        redisTemplate.opsForValue().set(prefix + email, otp, Duration.ofSeconds(otpExpirySeconds));
        redisTemplate.delete(attemptsKey(prefix, email));
        redisTemplate.opsForValue().set(
                cooldownKey(prefix, email), "1", Duration.ofSeconds(resendCooldownSeconds));

        return otp;
    }

    public boolean canResend(String prefix, String email) {
        return Boolean.FALSE.equals(redisTemplate.hasKey(cooldownKey(prefix, email)));
    }

    public long cooldownSecondsRemaining(String prefix, String email) {
        Long ttl = redisTemplate.getExpire(cooldownKey(prefix, email));
        return ttl == null || ttl < 0 ? 0 : ttl;
    }

    /**
     * Validates the OTP and consumes it on success (deletes the key so it
     * can't be replayed). On failure, increments the attempt counter and
     * locks out further attempts once maxAttempts is hit.
     */
    public void validateOtp(String prefix, String email, String submittedOtp) {
        String key = prefix + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new InvalidOtpException("OTP has expired or was never requested. Please request a new one.");
        }

        Long attempts = redisTemplate.opsForValue().increment(attemptsKey(prefix, email));
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey(prefix, email), Duration.ofSeconds(otpExpirySeconds));
        }
        if (attempts != null && attempts > maxAttempts) {
            redisTemplate.delete(key);
            throw new InvalidOtpException("Too many incorrect attempts. Please request a new OTP.");
        }

        if (!storedOtp.equals(submittedOtp)) {
            throw new InvalidOtpException("Incorrect OTP. Please try again.");
        }

        redisTemplate.delete(key);
        redisTemplate.delete(attemptsKey(prefix, email));
    }

    private String cooldownKey(String prefix, String email) {
        return prefix + "cooldown:" + email;
    }

    private String attemptsKey(String prefix, String email) {
        return prefix + "attempts:" + email;
    }
}