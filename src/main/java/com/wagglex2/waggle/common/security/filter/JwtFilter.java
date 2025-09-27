package com.wagglex2.waggle.common.security.filter;

import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Value("${security.whitelist}")
    private String[] whiteList;

    private final JwtUtil jwtUtil;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String uri = request.getRequestURI();

            // 프론트가 브라우저면 preflight 요청은 바로 통과
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            // 1. 화이트리스트는 그냥 통과
            if (isWhiteListed(uri)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 이미 인증된 경우 Skip
            Authentication existing =  SecurityContextHolder.getContext().getAuthentication();
            if (existing != null && !(existing instanceof AnonymousAuthenticationToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            // JWT Token 추출 (Authorization Header 또는 쿠키에서)
            String accessToken = extractAccessToken(request);

            // 토큰이 없으면 그냥 통과 (보호된 API라면 EntryPoint가 알아서 401 처리)
            if (!StringUtils.hasText(accessToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 한 번만 파싱해서 검증 / Claims 확보
            Claims claims;
            try {
                claims = jwtUtil.parseToken(accessToken);
            } catch (ExpiredJwtException ex) {
                log.debug("Access Token 만료 : {}", uri);
                request.setAttribute("token.error", "expired");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            } catch (JwtException ex) {
                log.debug("Access Token 위조/파싱 실패 : uri={}, message={}", uri, ex.getMessage());
                request.setAttribute("token.error", "invalid");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtUtil.isAccessToken(accessToken)) {
                log.debug("Access Token이 아닙니다: {}", uri);
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            setAuthenticationFromClaims(claims);
            log.debug("유효한 Access Token입니다. 요청한 URL : {}", uri);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("JWT 필터 처리 중 예외: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    private boolean isWhiteListed(String uri) {
        return Arrays.stream(whiteList)
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
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
    private void setAuthenticationFromClaims(Claims claims) {
        try {
            // JJWT는 정수형을 Integer로 줄 때가 있음 -> ClassCastException 가능성
            Number uidNum = claims.get("uid", Number.class);
            Long userId = uidNum != null ? uidNum.longValue() : null;
            String username = claims.get("username", String.class);
            String nickname = claims.get("nickname", String.class);
            String role = claims.get("role", String.class);

            // CustomUserDetails 생성
            CustomUserDetails userDetails = new CustomUserDetails(userId, username, nickname, role);

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