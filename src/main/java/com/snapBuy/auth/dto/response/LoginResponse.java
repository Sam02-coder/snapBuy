package com.snapBuy.auth.dto.response;

import com.snapBuy.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;

    /** True only for a merchant on their temp password - frontend must redirect to change-password. */
    private boolean firstLogin;
}