package com.wagglex2.waggle.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationRequestDto(
        @NotBlank(message = "이메일이 누락되었습니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증번호가 누락되었습니다.")
        @Pattern(regexp = "^[0-9]{6}", message = "인증번호는 숫자 6자리여야 합니다.")
        String inputCode
) {
}
