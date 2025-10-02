package com.wagglex2.waggle.domain.user.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.user.dto.request.PasswordRequestDto;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

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
     * @param dto 비밀번호 변경 요청 DTO (기존 비밀번호, 새 비밀번호, 확인 비밀번호 포함)
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
}
