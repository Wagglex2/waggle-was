package com.wagglex2.waggle.domain.common.dto.request;

import com.wagglex2.waggle.domain.project.dto.request.ProjectCommonRequestDto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 모집 공고 공통 요청 DTO
 * <p>
 * 모든 공고 요청 관련 DTO에서 공통으로 사용하는 필드를 포함한다.
 * 마감일은 자동으로 하루의 끝(23:59:59)으로 설정한다.
 * </p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <li>{@link #title} - 공고 제목</li>
 *     <li>{@link #content} - 공고 본문</li>
 *     <li>{@link #deadline} - 마감일</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>하위 클래스: {@link ProjectCommonRequestDto}</li>
 * </ul>
 */
@Getter
public abstract class BaseRecruitmentRequestDto {
    @NotBlank(message = "제목이 누락되었습니다.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private final String title;

    @NotBlank(message = "본문이 누락되었습니다.")
    @Size(max = 4096, message = "본문은 4096자를 초과할 수 없습니다.")
    private final String content;

    @NotNull(message = "마감일이 누락되었습니다.")
    @FutureOrPresent(message = "마감일은 오늘 이후여야 합니다.")
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
