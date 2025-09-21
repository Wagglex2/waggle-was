package com.wagglex2.waggle.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagglex2.waggle.common.jwt.JwtUtil;
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
            User user = userDetails.getUser();

            Long userId = user.getId();
            String username = user.getUsername();
            String nickname = user.getNickname();
            String role = user.getRole().name();

            // JWT (Access / Refresh) 발급
            String accessToken = jwtUtil.createAccessToken(userId, username, nickname, role);
            String refreshToken = jwtUtil.createRefreshToken(userId);

            // 기존 RefreshToken 삭제 (중복 로그인 방지)
            String redisKey = REFRESH_TOKEN_PREFIX + userId;
            redisTemplate.delete(redisKey);

            // 새로운 RefreshToken을 Redis에 저장
            long refreshExpMills = jwtUtil.getRefreshExpMills();
            redisTemplate.opsForValue().set(redisKey, refreshToken, refreshExpMills, TimeUnit.MILLISECONDS);

            log.info("Refresh token stored in Redis for user: {}", userId);

            // AccessToken을 쿠키에 담아 내려주기
            ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofMillis(jwtUtil.getAccessExpMills()))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 성공 응답 생성
            handleSuccess(response);

        } catch (Exception e) {
            log.error("Error during authentication success handling", e);
            handleError(response);
        }
    }

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

    private void handleError(HttpServletResponse response) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", "LOGIN_ERROR");
        responseBody.put("message", "로그인 처리 중 오류가 발생했습니다.");
        responseBody.put("data", null);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }
}
