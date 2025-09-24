package com.wagglex2.waggle.domain.auth.controller;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;

    private final static String REFRESH_TOKEN_PREFIX = "RT:";
    private final static String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private final static String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        // Cookie로부터 Refresh Token 추출
        String refreshToken = extractRefreshToken(request);

        if (!StringUtils.hasText(refreshToken) || !jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            log.warn("Refresh Token이 유효하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.REFRESH_TOKEN_INVALID));
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.hasText(storedRefreshToken) || !BCrypt.checkpw(refreshToken, storedRefreshToken)) {
            log.warn("Refresh Token이 일치하지 않습니다: {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.REFRESH_TOKEN_MISMATCH));
        }

        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.USER_NOT_FOUND));
        }

        // 새 Token 발급
        String newAccessToken = jwtUtil.createAccessToken(
                userId, user.getUsername(), user.getNickname(), user.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(userId);
        String newHashedRefreshToken = BCrypt.hashpw(newRefreshToken, BCrypt.gensalt());

        // Redis에 새로운 Refresh Token 갱신
        redisTemplate.opsForValue().set(redisKey, newHashedRefreshToken, jwtUtil.getRefreshExpMills(), TimeUnit.MILLISECONDS);

        // Cookie 설정
        setTokenCookie(response, newAccessToken, ACCESS_TOKEN_COOKIE_NAME, jwtUtil.getAccessExpMills() / 1000);
        setTokenCookie(response, newRefreshToken, REFRESH_TOKEN_COOKIE_NAME, jwtUtil.getRefreshExpMills() / 1000);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("토큰 재발급에 성공했습니다."));
    }

    /**
     * Cookie로부터 Refresh Token 추출하기
     */
    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 해당 Token에 맞게 쿠키 설정
     * @param token Refresh / Access Token
     * @param cookieName 쿠키 이름
     * @param maxAge 쿠키 만료 시간
     */
    private void setTokenCookie(HttpServletResponse response, String token, String cookieName, long maxAge) {
        String cookieValue = String.format(
                "%s=%s; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=%d",
                cookieName,
                token,
                maxAge
        );

        response.addHeader("Set-Cookie", cookieValue);
    }
}
