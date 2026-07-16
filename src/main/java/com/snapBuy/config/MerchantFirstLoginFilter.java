package com.snapBuy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapBuy.common.response.ApiResponse;
import com.snapBuy.merchant.repository.MerchantProfileRepository;
import com.snapBuy.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs after JwtAuthFilter. If the authenticated principal is a merchant
 * whose firstLogin flag is still true, every /api/v1/merchant/** endpoint
 * except the forced password-change endpoint is rejected with 403.
 */
@Component
@RequiredArgsConstructor
public class MerchantFirstLoginFilter extends OncePerRequestFilter {

    private static final String ALLOWED_PATH = "/api/v1/merchant/first-login/change-password";

    private final MerchantProfileRepository merchantProfileRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/api/v1/merchant/") || path.equals(ALLOWED_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean isFirstLogin = merchantProfileRepository.findByUserId(userDetails.getId())
                    .map(profile -> profile.isFirstLogin())
                    .orElse(false);

            if (isFirstLogin) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ApiResponse<Void> body = ApiResponse.error(
                        "You must change your temporary password before accessing other features.");
                response.getWriter().write(objectMapper.writeValueAsString(body));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}