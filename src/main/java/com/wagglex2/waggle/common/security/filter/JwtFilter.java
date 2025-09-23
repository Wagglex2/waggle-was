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
                    log.debug("Invalid or expired access token, attempting refresh");

                    // Access Token이 유효하지 않으면 Refresh Token으로 갱신 시도
                    handleTokenRefresh(request, response);
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
     * Refresh Token 추출 (Cookie에서)
     */
    private String extractRefreshToken(HttpServletRequest request) {
        return extractTokenFromCookie(request, REFRESH_TOKEN_COOKIE_NAME);
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

            log.debug("Authentication set for user: {} (ID: {})", username, userId );
        } catch (Exception e) {
            log.error("Error setting authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Access Token 만료 시 Refresh Token으로 갱신
     */
    private void handleTokenRefresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = extractRefreshToken(request);

            // Refresh Token이 비어있을 때
            if (!StringUtils.hasText(refreshToken)) {
                log.debug("No refresh token found");
                SecurityContextHolder.clearContext();
                return;
            }

            // Refresh Token 검증 (서명 + 만료 확인)
            if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                log.debug("Invalid refresh token");
                clearTokenCookies(response);
                SecurityContextHolder.clearContext();
                return;
            }

            // Redis에서 Refresh Token 확인
            Long userId = jwtUtil.getUserId(refreshToken);
            String redisKey = REFRESH_TOKEN_PREFIX + userId;
            String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);

            // 원본과 hash된 Refresh Token 비교
            if (!StringUtils.hasText(storedRefreshToken) || !BCrypt.checkpw(refreshToken, storedRefreshToken)) {
                log.warn("Refresh token mismatch for user: {}", userId);
                clearTokenCookies(response);
                SecurityContextHolder.clearContext();
                return;
            }

            // 새로운 Access Token 발급
            User user = userService.findById(userId);
            if (user == null) {
                log.warn("User not found during token refresh: {}", userId);
                clearTokenCookies(response);
                SecurityContextHolder.clearContext();
                return;
            }

            String newAccessToken = jwtUtil.createAccessToken(
                    user.getId(), user.getUsername(), user.getNickname(),user.getRole().name());


            // Refresh Token 회전
            String newRefreshToken = jwtUtil.createRefreshToken(userId);
            String newHashedRefreshToken = BCrypt.hashpw(newRefreshToken, BCrypt.gensalt());
            // set만 하더라도 원래 있던 value를 덮어씀 -> 굳이 delete할 필요 없음
            redisTemplate.opsForValue().set(redisKey, newHashedRefreshToken, jwtUtil.getRefreshExpMills(), TimeUnit.MILLISECONDS);

            // 새로운 Access Token을 쿠키에 설정
            setTokenCookie(response, newAccessToken, ACCESS_TOKEN_COOKIE_NAME);
            setTokenCookie(response, newRefreshToken, REFRESH_TOKEN_COOKIE_NAME);

            // 인증 설정
            setAuthentication(newAccessToken);
            log.info("Access token refreshed for user: {}", userId);

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
        }
    }

    /**
     * 새로운 Access / Refresh Token 쿠키 설정
     */
    private void setTokenCookie(HttpServletResponse response, String token, String cookieName) {

        String cookieValue = "";

        if (cookieName.equals(ACCESS_TOKEN_COOKIE_NAME)) {
            cookieValue = String.format(
                    "%s=%s; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=%d",
                    ACCESS_TOKEN_COOKIE_NAME,
                    token,
                    jwtUtil.getAccessExpMills() / 1000
            );
        }

        if (cookieName.equals(REFRESH_TOKEN_COOKIE_NAME)) {
            cookieValue = String.format(
                    "%s=%s; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=%d",
                    REFRESH_TOKEN_COOKIE_NAME,
                    token,
                    jwtUtil.getRefreshExpMills() / 1000
            );
        }

        response.addHeader("Set-Cookie", cookieValue);
    }

    /**
     * Token Cookie 제거
     */
    private void clearTokenCookies(HttpServletResponse response) {
        // Access Token Cookie 제거
        String clearAccessCookie = String.format(
                "%s=; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=0",
                ACCESS_TOKEN_COOKIE_NAME
        );

        // Refresh Token Cookie 제거
        String clearRefreshCookie = String.format(
                "%s=; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=0",
                REFRESH_TOKEN_COOKIE_NAME
        );

        response.addHeader("Set-Cookie", clearAccessCookie);
        response.addHeader("Set-Cookie", clearRefreshCookie);
    }
}
