package com.wagglex2.waggle.domain.auth.dto.request;

import jakarta.validation.constraints.Email;

public record EmailRequestDto(
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {
}
