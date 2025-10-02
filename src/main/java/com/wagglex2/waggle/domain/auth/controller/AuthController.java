package com.wagglex2.waggle.domain.auth.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.auth.dto.request.EmailRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.EmailVerificationRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignInRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.auth.dto.response.TokenPair;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserService userService;

    /**
     * 회원가입 이메일 인증을 위한 인증번호를 발송한다.
     *
     * @param dto 인증번호를 받을 사용자 이메일
     * @return ApiResponse(Void) — 성공 시 "이메일 전송에 성공했습니다."
     */
    @PostMapping("/email/code")
    public ResponseEntity<ApiResponse<Void>> sendEmailAuthCode(
            @Valid @RequestBody EmailRequestDto dto
            ) {
        authService.sendAuthCode(dto.email());

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
    public ResponseEntity<ApiResponse<Void>> verifyAuthCode(
            @Valid @RequestBody EmailVerificationRequestDto dto
    ) {
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
    public ResponseEntity<ApiResponse<Long>> signUp(
            @Valid @RequestBody SignUpRequestDto dto
    ) {
        Long userId = userService.signUp(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입에 성공했습니다.", userId));
    }

    /**
     * 로그인 요청을 처리한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>요청 DTO를 {@code @Valid}로 검증</li>
     *     <li>{@link AuthService#login(SignInRequestDto)} 호출 → Access Token 및 Refresh Token 발급</li>
     *     <li>Access Token을 HTTP 응답 헤더 {@code Authorization}에 추가</li>
     *     <li>Refresh Token을 HttpOnly 쿠키로 추가</li>
     *     <li>HTTP 상태코드 {@code 200 OK}와 함께 ApiResponse로 응답</li>
     * </ol>
     *
     * @param dto      로그인 요청 DTO (username, password)
     * @param response HttpServletResponse (헤더 및 쿠키 추가용)
     * @return 로그인 성공 메시지를 포함한 응답
     * @see AuthService#login(SignInRequestDto)
     */
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<Void>> signIn(
            @Valid @RequestBody SignInRequestDto dto,
            HttpServletResponse response
    ) {

        // 1. 로그인 처리
        TokenPair tokens = authService.login(dto);

        // 2. Access Token -> 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokens.accessToken());

        // 3. Refresh Token -> 쿠키에 추가
        addCookie(response,
                tokens.refreshToken(),
                REFRESH_TOKEN_COOKIE_NAME,
                jwtUtil.getRefreshExpMills() / 1000
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("로그인에 성공했습니다."));
    }

    @PostMapping("/sign-out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> signOut(HttpServletResponse response,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        authService.deleteRefreshToken(userId);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("로그아웃 성공 : userId = {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("로그아웃에 성공했습니다."));
    }

    /**
     * Refresh Token을 사용해 새로운 Access/Refresh Token을 재발급한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *   <li>쿠키에서 Refresh Token 추출</li>
     *   <li>AuthService를 통해 토큰 재발급</li>
     *   <li>새로운 토큰을 쿠키로 설정</li>
     *   <li>성공 응답 반환</li>
     * </ol>
     *
     * @param refreshToken 쿠키에 담긴 Refresh Token
     * @param response     새로운 토큰을 담아 보낼 HTTP 응답
     * @return ApiResponse(Void) — 성공 시 "토큰 재발급에 성공했습니다."
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        // 1. 토큰 재발급
        TokenPair tokens = authService.reissueTokens(refreshToken);

        // 2. Access Token -> 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokens.accessToken());

        // 2. Refresh Token -> 쿠키 설정
        addCookie(response,
                tokens.refreshToken(),
                REFRESH_TOKEN_COOKIE_NAME,
                jwtUtil.getRefreshExpMills() / 1000
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("토큰 재발급에 성공했습니다."));
    }

    /**
     * 주어진 토큰을 응답 쿠키에 설정한다.
     *
     * <p>설정 옵션:</p>
     * <ul>
     *   <li><b>HttpOnly</b>: true (JS에서 접근 불가, XSS 방어)</li>
     *   <li><b>Secure</b>: true (HTTPS에서만 전송)</li>
     *   <li><b>SameSite</b>: Lax (기본 CSRF 방어)</li>
     *   <li><b>Path</b>: "/" (애플리케이션 전역에서 사용 가능)</li>
     *   <li><b>Max-Age</b>: 토큰 만료 시간(초)</li>
     * </ul>
     *
     * @param response   HTTP 응답
     * @param token      저장할 토큰 값
     * @param cookieName 쿠키 이름
     * @param maxAge     만료 시간(초)
     */
    private void addCookie(
            HttpServletResponse response,
            String token,
            String cookieName,
            long maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
