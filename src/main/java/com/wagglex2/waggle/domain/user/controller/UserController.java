package com.wagglex2.waggle.domain.user.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(@RequestParam
                                                                 @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다.")
                                                                 String username) {
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
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(@RequestParam
                                                              @NotBlank(message = "이메일이 누락되었습니다.")
                                                              @Email(message = "올바른 이메일 형식이 아닙니다.")
                                                              String email) {

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
                                                                 @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
                                                                 String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("이미 사용 중인 닉네임입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("사용 가능한 닉네임입니다.", false));
        }
    }
}
