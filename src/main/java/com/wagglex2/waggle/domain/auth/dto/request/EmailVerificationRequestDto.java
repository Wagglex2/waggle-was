package com.wagglex2.waggle.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailVerificationRequestDto(
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증번호가 누락되었습니다.")
        @Size(min = 6, max = 6, message = "인증번호는 6자입니다.")
        String inputCode
) {
}
