package com.wagglex2.waggle.domain.user.controller;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import com.wagglex2.waggle.domain.user.dto.request.PasswordRequestDto;
import com.wagglex2.waggle.domain.user.dto.request.UserUpdateRequestDto;
import com.wagglex2.waggle.domain.user.dto.request.WithdrawRequestDto;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final UserService userService;
    private final ReviewService reviewService;

    /**
     * 아이디 중복 여부를 검사한다.
     *
     * @param username 검사할 사용자 로그인 ID (영문, 숫자, 언더스코어 4~20자)
     * @return ApiResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/username/check")
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(
            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다.")
            String username
    ) {
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
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(
            @RequestParam
            @NotBlank(message = "이메일이 누락되었습니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email
    ) {

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
    public ResponseEntity<ApiResponse<Boolean>> existsByNickname(
            @RequestParam
            @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
            String nickname
    ) {
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
     * 비밀번호를 변경한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *   <li>요청으로부터 기존 비밀번호, 새 비밀번호, 확인 비밀번호를 전달받는다.</li>
     *   <li>인증된 사용자(@AuthenticationPrincipal)에서 userId를 추출한다.</li>
     *   <li>서비스 계층(userService.changePassword)에서 비밀번호 검증 및 변경 로직을 처리한다.</li>
     *   <li>비밀번호 변경 성공 시 성공 응답(ApiResponse<Void>)을 반환한다.</li>
     * </ol>
     *
     * @param dto         비밀번호 변경 요청 DTO (기존 비밀번호, 새 비밀번호, 확인 비밀번호 포함)
     * @param userDetails 현재 인증된 사용자 정보
     * @return ApiResponse<Void> — 성공 메시지를 담은 OK(200) 응답
     */
    @PostMapping("/me/password-change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> passwordChange(
            @Valid @RequestBody PasswordRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.changePassword(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("비밀번호 변경에 성공했습니다."));
    }

    /**
     * 현재 로그인한 사용자의 정보를 조회한다.
     *
     * <p>처리 흐름:</p>
     * <ol>
     *   <li>Spring Security의 {@code @AuthenticationPrincipal}을 통해 인증된 사용자 정보(CustomUserDetails) 획득</li>
     *   <li>해당 userId를 기반으로 {@code userService.getUserInfo()} 호출 → UserResponseDto 변환</li>
     *   <li>ApiResponse 래핑을 통해 일관된 응답 형식으로 반환</li>
     * </ol>
     *
     * @param userDetails 현재 인증된 사용자 정보 (CustomUserDetails)
     * @return 현재 로그인한 사용자의 UserResponseDto 응답
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDto data = userService.getUserInfo(userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("회원정보를 불러오는데 성공했습니다.", data));
    }

    /**
     * 현재 로그인한 사용자의 프로필 정보를 수정한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>Spring Security의 {@code @AuthenticationPrincipal}을 통해 인증된 사용자 정보(CustomUserDetails) 획득</li>
     *   <li>수정 요청 DTO({@link UserUpdateRequestDto})를 기반으로 {@code userService.updateUserInfo()} 호출</li>
     *   <li>엔티티 업데이트 후 {@link UserResponseDto}로 변환</li>
     *   <li>{@link ApiResponse} 래핑을 통해 최신 사용자 정보 반환</li>
     * </ol>
     *
     * @param userDetails 현재 인증된 사용자 정보 (CustomUserDetails)
     * @param dto         수정 요청 DTO ({@link UserUpdateRequestDto})
     * @return 수정된 사용자 프로필 정보 ({@link UserResponseDto})를 담은 응답
     */
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequestDto dto
    ) {
        UserResponseDto data = userService.updateUserInfo(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("회원정보를 수정하는데 성공했습니다.", data));
    }

    /**
     * 회원 탈퇴 API
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>인증된 사용자(@AuthenticationPrincipal) 정보를 가져옴</li>
     *   <li>요청 본문으로 전달된 {@link WithdrawRequestDto}에서 비밀번호를 검증</li>
     *   <li>서비스 계층 {@code userService.withdraw()} 호출 → 비밀번호 확인, 소프트 삭제, Refresh Token 제거</li>
     *   <li>추가적으로 응답 쿠키에서 Refresh Token을 만료 처리 (Max-Age=0)</li>
     *   <li>탈퇴 성공 메시지를 포함한 200 OK 응답 반환</li>
     * </ol>
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Principal)
     * @param dto         탈퇴 요청 DTO (비밀번호 포함)
     * @param response    HTTP 응답 객체 (쿠키 만료 처리용)
     * @return {@link ApiResponse} 성공 메시지 (200 OK)
     */
    @DeleteMapping("/me/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody WithdrawRequestDto dto,
            HttpServletResponse response
    ) {
        userService.withdraw(userDetails.getUserId(), dto.password());

        addCookie(response, "", REFRESH_TOKEN_COOKIE_NAME, 0);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("회원탈퇴에 성공했습니다."));
    }

    /**
     * 특정 사용자가 받은 리뷰 목록을 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>요청 경로의 userId로 대상 사용자 존재 여부 확인 → 없으면 {@link BusinessException} 발생</li>
     *   <li>요청한 pageNo가 1 미만이면 {@link BusinessException} 발생</li>
     *   <li>페이지 번호는 JPA의 PageRequest 기준(0부터 시작)이므로 <code>pageNo - 1</code>로 조정</li>
     *   <li>서비스 계층에서 해당 사용자의 리뷰를 조회하고 DTO로 변환</li>
     *   <li>성공 시 {@link ApiResponse} 형태로 200 OK 응답 반환</li>
     * </ol>
     *
     * @param userId  리뷰 대상 사용자의 ID (경로 변수)
     * @param pageNo  요청한 페이지 번호 (1부터 시작, 기본값 1)
     * @return        리뷰 목록(Page<ReviewResponseDto>)을 포함한 200 OK 응답
     * @throws BusinessException 사용자 미존재 또는 잘못된 페이지 번호일 경우 발생
     */
    @GetMapping("/{userId}/reviews/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getReviews(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int pageNo
    ) {
        if (!userService.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (pageNo < 1) {
            throw new BusinessException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Page<ReviewResponseDto> data = reviewService.getReviewsByRevieweeId(userId, pageNo - 1);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("리뷰 조회에 성공했습니다.", data));
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
