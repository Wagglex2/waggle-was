package com.wagglex2.waggle.common.security.filter;

import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 이미 인증된 경우 Skip
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // JWT Token 추출 (Authorization Header 또는 쿠키에서)
            String accessToken = extractAccessToken(request);

            if (StringUtils.hasText(accessToken)) {
                // Access Token 검증 및 인증 처리
                if (jwtUtil.validateToken(accessToken) && jwtUtil.isAccessToken(accessToken)) {
                    setAuthentication(accessToken);
                    log.debug("Valid access token found, authentication set for request: {}", request.getRequestURL());
                } else {
                    log.debug("Invalid or expired access token");
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                log.debug("No access token found in request: {}", request.getRequestURL());
            }
        } catch (Exception e) {
            log.error("JWT authentication error: {}", e.getMessage(), e);

            // 인증 오류 시 SecurityContext Clear
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Access Token 추출 (Authorization Header 또는 Cookie에서)
     */
    private String extractAccessToken(HttpServletRequest request) {
        // 1. Authorization Header에서 추출
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. Cookie에서 추출
        return extractTokenFromCookie(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    /**
     * 쿠키에서 토큰 추출
     * @param cookieName ACCESS_TOKEN_COOKIE_NAME, REFRESH_TOKEN_COOKIE_NAME
     */
    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }

    /**
     * JWT Token으로 인증 객체 생성 및 SecurityContext에 저장
     */
    private void setAuthentication(String accessToken) {
        try {
            Claims claims = jwtUtil.parseToken(accessToken);
            Long userId = claims.get("uid", Long.class);
            String username = claims.get("username", String.class);

            User user = userService.findById(userId);

            if (user == null) {
                log.warn("User not found for ID: {}", userId);
                return;
            }

            // CustomUserDetails 생성
            CustomUserDetails userDetails = new CustomUserDetails(user);

            // Authentication 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authentication set for user: {} (ID: {})", username, userId);
        } catch (Exception e) {
            log.error("Error setting authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}