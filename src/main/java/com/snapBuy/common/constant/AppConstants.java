package com.snapBuy.common.constant;

public final class AppConstants {

    private AppConstants() {
        // utility class - no instantiation
    }

    // Pagination defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    public static final int MAX_PAGE_SIZE = 50;

    // Redis key prefixes
    public static final String OTP_KEY_PREFIX = "otp:register:";
    public static final String OTP_RESEND_COOLDOWN_PREFIX = "otp:cooldown:";
    public static final String OTP_ATTEMPTS_PREFIX = "otp:attempts:";
    public static final String FORGOT_PASSWORD_OTP_PREFIX = "otp:forgot-password:";
    public static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    public static final String LOGIN_ATTEMPT_PREFIX = "login:attempts:";

    // Security
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOGIN_LOCKOUT_MINUTES = 15;

    // Misc
    public static final String TEMP_PASSWORD_CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$";
    public static final int TEMP_PASSWORD_LENGTH = 12;
}