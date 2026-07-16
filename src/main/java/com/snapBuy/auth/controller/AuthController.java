package com.snapBuy.auth.controller;

import com.snapBuy.auth.dto.request.ForgotPasswordRequest;
import com.snapBuy.auth.dto.request.LoginRequest;
import com.snapBuy.auth.dto.request.OtpVerifyRequest;
import com.snapBuy.auth.dto.request.RefreshTokenRequest;
import com.snapBuy.auth.dto.request.RegisterRequest;
import com.snapBuy.auth.dto.request.ResendOtpRequest;
import com.snapBuy.auth.dto.request.ResetPasswordRequest;
import com.snapBuy.auth.dto.response.LoginResponse;
import com.snapBuy.auth.dto.response.TokenPairResponse;
import com.snapBuy.auth.service.AuthService;
import com.snapBuy.common.constant.AppConstants;
import com.snapBuy.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Registration, login, OTP, and password recovery")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. Please check your email for the OTP."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully. You can now log in."));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("A new OTP has been sent to your email."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenPairResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        TokenPairResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = AppConstants.JWT_HEADER, required = false) String authHeader,
            @Valid @RequestBody RefreshTokenRequest request) {

        String accessToken = (authHeader != null && authHeader.startsWith(AppConstants.JWT_PREFIX))
                ? authHeader.substring(AppConstants.JWT_PREFIX.length())
                : null;

        authService.logout(accessToken, request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("If an account exists with this email, a reset code has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please log in again."));
    }
}