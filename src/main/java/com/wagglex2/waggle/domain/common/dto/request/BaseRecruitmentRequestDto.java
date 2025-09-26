package com.wagglex2.waggle.domain.common.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseRecruitmentRequestDto {
    @NotBlank(message = "제목이 누락되었습니다.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private final String title;

    @NotBlank(message = "본문이 누락되었습니다.")
    @Size(max = 4096, message = "본문은 4096자를 초과할 수 없습니다.")
    private final String content;

    @NotNull(message = "마감일이 누락되었습니다.")
    @FutureOrPresent(message = "마감일은 오늘 또는 미래여야 합니다.")
    private final LocalDateTime deadline;

    protected BaseRecruitmentRequestDto(String title, String content, LocalDateTime deadline) {
        this.title = title;
        this.content = content;
        // 마감 시간 23시 59분 59초로 고정
        this.deadline = deadline.withHour(23)
                                .withMinute(59)
                                .withSecond(59);
    }
}
