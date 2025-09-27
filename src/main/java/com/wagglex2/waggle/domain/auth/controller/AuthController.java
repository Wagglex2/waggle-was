package com.wagglex2.waggle.domain.auth.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.domain.auth.dto.request.EmailVerificationRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 회원가입 이메일 인증을 위한 인증번호를 발송한다.
     *
     * @param email 인증번호를 받을 사용자 이메일
     * @return ApiResponse(Void) — 성공 시 "이메일 전송에 성공했습니다."
     */
    @PostMapping("/email/code")
    public ResponseEntity<ApiResponse<Void>> sendEmailAuthCode(@RequestParam
                                                               @Email(message = "올바른 이메일 형식이 아닙니다.")
                                                               String email) {
        authService.sendAuthCode(email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("이메일 전송에 성공했습니다."));
    }

    /**
     * 사용자가 입력한 인증번호를 검증한다.
     *
     * @param dto 이메일과 인증번호를 담은 DTO
     * @return ApiResponse(Void) — 성공 시 "이메일 인증이 완료되었습니다."
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAuthCode(@Valid @RequestBody EmailVerificationRequestDto dto) {
        authService.verifyCode(dto.email(), dto.inputCode());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("이메일 인증이 완료되었습니다."));
    }

    /**
     * 회원가입 요청을 처리한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>요청 DTO를 {@code @Valid}로 검증</li>
     *     <li>검증 성공 시 {@link UserService#signUp(SignUpRequestDto)} 호출</li>
     *     <li>생성된 사용자 ID 반환</li>
     *     <li>HTTP 상태코드 {@code 201 Created}와 함께 ApiResponse로 응답</li>
     * </ol>
     *
     * @param dto 회원가입 요청 DTO
     * @return 회원가입 성공 메시지와 생성된 사용자 ID를 포함한 응답
     * @see UserService#signUp(SignUpRequestDto)
     */
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUpRequestDto dto) {
        Long userId = userService.signUp(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입에 성공했습니다.", userId));
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 재발급한다.
     *
     * @param request  Refresh Token이 담긴 HTTP 요청
     * @param response 새로운 토큰을 쿠키에 담아 반환할 HTTP 응답
     * @return ApiResponse(Void) — 성공 시 "토큰 재발급에 성공했습니다."
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.issueNewTokens(request, response);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("토큰 재발급에 성공했습니다."));
    }
}
