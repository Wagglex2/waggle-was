package com.wagglex2.waggle.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        try {
            // CustomUserDetails에서 User 정보 직접 가져오기
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Long userId = userDetails.getUserId();
            String username = userDetails.getUsername();
            String nickname = userDetails.getNickname();
            String role = userDetails.getRole();

            // JWT (Access / Refresh) 발급
            String accessToken = jwtUtil.createAccessToken(userId, username, nickname, role);
            String refreshToken = jwtUtil.createRefreshToken(userId);

            // 기존 RefreshToken 삭제 (중복 로그인 방지)
            String redisKey = REFRESH_TOKEN_PREFIX + userId;
            redisTemplate.delete(redisKey);

            // 새로운 RefreshToken을 Redis에 저장 -> 해시 후 저장 (Redis 유출되더라도 안전함)
            String hashedRefreshToken = BCrypt.hashpw(refreshToken, BCrypt.gensalt());
            long refreshExpMills = jwtUtil.getRefreshExpMills();
            redisTemplate.opsForValue().set(redisKey, hashedRefreshToken, refreshExpMills, TimeUnit.MILLISECONDS);

            log.info("Refresh token stored in Redis for user: {}", userId);

            // 쿠키 생성 및 응답에 추가
            addTokenCookieToResponse(response, accessToken, refreshToken);

            // 성공 응답 생성
            handleSuccess(response);

        } catch (ClassCastException e) {
            log.error("Authentication principal is not CustomUserDetails", e);
            handleError(response, "인증 정보가 올바르지 않습니다.");
        } catch (Exception e) {
            log.error("Error during authentication success handling", e);
            handleError(response, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 응답 Cookie에 Access / Refresh Token 담기
     */
    private void addTokenCookieToResponse(HttpServletResponse response,
                                          String accessToken,
                                          String refreshToken) {

        // Access Token Cookie 생성
        ResponseCookie accessCookie = createResponseCookie(
                ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                Duration.ofMillis(jwtUtil.getAccessExpMills())
        );

        // Refresh Token Cookie 생성
        ResponseCookie refreshCookie = createResponseCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                Duration.ofMillis(jwtUtil.getRefreshExpMills())
        );

        // Cookie를 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    /**
     * Refresh / Access Token 쿠키에 담기
     * @param name Refresh / Access Token Cookie Name
     * @param value Refresh / Access Token 값
     * @param maxAge Refresh / Access 만료 기간
     */
    private ResponseCookie createResponseCookie(String name, String value, Duration maxAge) {

        // Lax로 설정, 쿠키가 안붙으면 None으로 바꿔주기
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    /**
     * 로그인 성공했을 때 JSON 반환값 설정
     */
    private void handleSuccess(HttpServletResponse response) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", "SUCCESS");
        responseBody.put("message", "로그인에 성공했습니다.");
        responseBody.put("data", null);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }

    /**
     * 로그인 실패했을 때 JSON 반환값 설정
     */
    private void handleError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", "LOGIN_ERROR");
        responseBody.put("message", message);
        responseBody.put("data", null);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }
}
