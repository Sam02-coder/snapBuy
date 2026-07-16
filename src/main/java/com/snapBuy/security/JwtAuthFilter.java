package com.snapBuy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.snapBuy.common.constant.AppConstants;

import java.io.IOException;

/**
 * Extracts the Bearer token, validates it, and (if valid and not blacklisted)
 * sets the SecurityContext so downstream @PreAuthorize checks work.
 * Does NOT reject requests with no token - permitAll endpoints rely on that;
 * SecurityConfig's authorizeHttpRequests is what actually enforces access.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AppConstants.JWT_HEADER);

        if (header == null || !header.startsWith(AppConstants.JWT_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(AppConstants.JWT_PREFIX.length());

        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(AppConstants.REFRESH_TOKEN_BLACKLIST_PREFIX + token))) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtUtil.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

                if (jwtUtil.isTokenValid(token, customUserDetails)
                        && "ACCESS".equals(jwtUtil.extractTokenType(token))) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ex) {
            log.debug("JWT authentication skipped: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}