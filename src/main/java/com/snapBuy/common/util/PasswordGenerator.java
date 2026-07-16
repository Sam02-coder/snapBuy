package com.snapBuy.common.util;

import java.security.SecureRandom;

import com.snapBuy.common.constant.AppConstants;

public final class PasswordGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {
    }

    public static String generateTempPassword() {
        StringBuilder sb = new StringBuilder(AppConstants.TEMP_PASSWORD_LENGTH);
        String chars = AppConstants.TEMP_PASSWORD_CHARACTERS;
        for (int i = 0; i < AppConstants.TEMP_PASSWORD_LENGTH; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}