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
import jakarta.validation.constraints.Pattern;
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
     * 아이디 중복 여부를 검사한다.
     *
     * @param username 검사할 사용자 로그인 ID (영문, 숫자, 언더스코어 4~20자)
     * @return ApiResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/username/check")
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
     * 이메일 중복 여부를 검사한다.
     *
     * @param email 검사할 사용자 이메일
     * @return ApiResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/email/check")
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

    /**
     * 닉네임 중복 여부를 검사한다.
     *
     * @param nickname 검사할 사용자 닉네임
     * @return ApiResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/nickname/check")
    public ResponseEntity<ApiResponse<Boolean>> existsByNickname(@RequestParam
                                                                 @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$") String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("이미 사용 중인 닉네임입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("사용 가능한 닉네임입니다.", false));
        }
    }

    /**
     * 회원가입 이메일 인증을 위한 인증번호를 발송한다.
     *
     * @param email 인증번호를 받을 사용자 이메일
     * @return ApiResponse(Void) — 성공 시 "이메일 전송에 성공했습니다."
     */
    @PostMapping("/email/code")
    public ResponseEntity<ApiResponse<Void>> sendEmailAuthCode(@RequestParam String email) {
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
     *     <li>검증 성공 시 {@link AuthService#signUp(SignUpRequestDto)} 호출</li>
     *     <li>생성된 사용자 ID 반환</li>
     *     <li>HTTP 상태코드 {@code 201 Created}와 함께 ApiResponse로 응답</li>
     * </ol>
     *
     * @param dto 회원가입 요청 DTO
     * @return 회원가입 성공 메시지와 생성된 사용자 ID를 포함한 응답
     * @see AuthService#signUp(SignUpRequestDto)
     */
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUpRequestDto dto) {
        Long userId = authService.signUp(dto);

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
