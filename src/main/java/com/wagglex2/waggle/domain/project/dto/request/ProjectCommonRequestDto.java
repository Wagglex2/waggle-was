package com.wagglex2.waggle.domain.project.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.request.BaseRecruitmentRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Set;

/**
 * 프로젝트 공고 공통 요청 DTO
 * <p>
 * 프로젝트 공고 생성 및 수정 DTO에서 공통으로 사용하는 필드를 포함한다.
 * 마감일이 프로젝트 종료일보다 늦지 않도록 검증한다.
 * </p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <b>상위 클래스 필드</b>
 *     <li>{@link #title} - 공고 제목</li>
 *     <li>{@link #content} - 공고 본문</li>
 *     <li>{@link #deadline} - 마감일</li>
 *     <br>
 *     <b>현재 클래스 필드</b>
 *     <li>{@link #purpose} - 프로젝트 목적</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #skills} - 기술 스택</li>
 *     <li>{@link #grades} - 모집 학년</li>
 *     <li>{@link #period} - 프로젝트 기간</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>상위 클래스: {@link BaseRecruitmentRequestDto}</li>
 *     <li>하위 클래스: {@link ProjectCreationRequestDto}</li>
 * </ul>
 */
@Getter
public abstract class ProjectCommonRequestDto extends BaseRecruitmentRequestDto {
    @NotNull(message = "프로젝트 목적이 누락되었습니다.")
    private final ProjectPurpose purpose;

    @NotNull(message = "진행 방식이 누락되었습니다.")
    private final MeetingType meetingType;

    @NotEmpty(message = "기술 스택이 누락되었습니다.")
    private final Set<Skill> skills;

    @NotEmpty(message = "모집 학년이 누락되었습니다.")
    @Size(max = 4, message = "최대 4개의 학년만 선택할 수 있습니다.")
    @Valid
    private final Set<GradeRequestDto> grades;

    @Valid
    private final PeriodRequestDto period;

    protected ProjectCommonRequestDto(
            String title, String content, LocalDateTime deadline,
            ProjectPurpose purpose, MeetingType meetingType, Set<Skill> skills,
            Set<GradeRequestDto> grades, PeriodRequestDto period
    ) {
        super(title, content, deadline);
        this.purpose = purpose;
        this.meetingType = meetingType;
        this.skills = skills;
        this.grades = grades;
        this.period = period;
    }

    public void validate() {
        if (getDeadline().isAfter(
                ChronoLocalDateTime.from(period.endDate()))
        ) {
            throw new BusinessException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "마감일은 종료일 이전이어야 합니다."
            );
        }
    }
}
