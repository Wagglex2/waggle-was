package com.wagglex2.waggle.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreationRequestDto(
        @NotNull(message = "대상 유저 ID가 누락되었습니다.")
        Long userTargetId,

        @NotBlank(message = "후기 내용이 누락되었습니다.")
        @Size(max = 100, message = "내용은 100자 이내로 입력해주세요.")
        String content
) {
}
