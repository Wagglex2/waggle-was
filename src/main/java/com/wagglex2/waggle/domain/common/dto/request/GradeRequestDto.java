package com.wagglex2.waggle.domain.common.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record GradeRequestDto(
        @NotNull(message = "모집 학년이 누락되었습니다.")
        @Range(min = 1, max = 4, message = "모집 학년은 1 이상 4 이하여야 합니다.")
        Integer grade
) { }
