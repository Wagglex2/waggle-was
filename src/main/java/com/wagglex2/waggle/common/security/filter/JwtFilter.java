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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

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

            if (!StringUtils.hasText(accessToken)) {
                log.debug("요청에서 Access Token을 찾을 수 없습니다: {}", request.getRequestURL());
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
                log.debug("Access Token이 만료되거나 유효하지 않습니다.");
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            setAuthentication(accessToken);
            log.debug("유효한 Access Token입니다. 요청한 URL : {}", request.getRequestURL());

        } catch (Exception e) {
            log.error("JWT 인증 중 오류가 발생했습니다.: {}", e.getMessage(), e);

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
                log.warn("User를 찾을 수 없습니다: {}", userId);
                return;
            }

            // CustomUserDetails 생성
            CustomUserDetails userDetails = new CustomUserDetails(user);

            // Authentication 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("유저 {}의 인증 설정을 성공했습니다. (ID: {})", username, userId);
        } catch (Exception e) {
            log.error("인증을 설정하는데 실패했습니다: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}