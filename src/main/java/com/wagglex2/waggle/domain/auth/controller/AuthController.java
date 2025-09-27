package com.wagglex2.waggle.domain.auth.controller;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.auth.dto.request.EmailVerificationRequestDto;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;

    private final static String REFRESH_TOKEN_PREFIX = "RT:";
    private final static String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private final static String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    /**
     * 중복된 아이디인지 검사한다.
     * 중복된 아이디일 경우 true, 사용 가능한 아이디일 경우 false를 반환한다.
     * @param username 사용자 로그인 ID
     */
    @GetMapping("/username-check")
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(@RequestParam
                                                                 @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$") String username) {
        boolean exists = userService.existsByUsername(username);

        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("이미 사용 중인 아이디입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("사용 가능한 아이디입니다.", false));
        }
    }

    /**
     * 중복된 이메일인지 검사한다.
     * 중복된 이메일일 경우 true, 사용 가능한 이메일일 경우 false를 반환한다.
     * @param email 사용할 이메일
     */
    @GetMapping("/email-check")
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(@RequestParam @Email String email) {

        boolean exists = userService.existsByEmail(email);

        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("이미 사용 중인 이메일입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("사용 가능한 이메일입니다.", false));
        }
    }

    @PostMapping("/email/code")
    public ResponseEntity<ApiResponse<Void>> sendEmailAuthCode(@RequestParam String email) {
        authService.sendAuthCode(email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("이메일 전송에 성공했습니다."));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAuthCode(@Valid @RequestBody EmailVerificationRequestDto dto) {
        authService.verifyCode(dto.email(), dto.inputCode());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("이메일 인증이 완료되었습니다."));
    }

    /**
     * Refresh Token을 사용해 Access Token 설정하기
     */
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
