package com.snapBuy.auth.service;

import com.snapBuy.auth.dto.request.ForgotPasswordRequest;
import com.snapBuy.auth.dto.request.LoginRequest;
import com.snapBuy.auth.dto.request.OtpVerifyRequest;
import com.snapBuy.auth.dto.request.RegisterRequest;
import com.snapBuy.auth.dto.request.ResendOtpRequest;
import com.snapBuy.auth.dto.request.ResetPasswordRequest;
import com.snapBuy.auth.dto.response.LoginResponse;
import com.snapBuy.auth.dto.response.TokenPairResponse;

public interface AuthService {

    void register(RegisterRequest request);

    void verifyOtp(OtpVerifyRequest request);

    void resendOtp(ResendOtpRequest request);

    LoginResponse login(LoginRequest request);

    TokenPairResponse refreshToken(String refreshToken);

    void logout(String accessToken, String refreshToken);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}