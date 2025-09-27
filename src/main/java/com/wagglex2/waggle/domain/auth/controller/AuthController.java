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

    private final AuthService authService;
    private final UserService userService;

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
        authService.issueNewTokens(request, response);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("토큰 재발급에 성공했습니다."));
    }
}
