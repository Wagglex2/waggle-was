package com.wagglex2.waggle.domain.project.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.request.BaseRecruitmentRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PositionInfoCreationRequestDto;
import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ProjectCreationRequestDto extends BaseRecruitmentRequestDto {
    @NotNull(message = "프로젝트 목적이 누락되었습니다.")
    private final ProjectPurpose purpose;

    @NotNull(message = "진행 방식이 누락되었습니다.")
    private final MeetingType meetingType;

    @NotEmpty(message = "포지션 정보가 누락되었습니다.")
    @Valid
    private final Set<PositionInfoCreationRequestDto> positions;

    @NotEmpty(message = "기술 스택이 누락되었습니다.")
    private final Set<Skill> skills;

    @NotEmpty(message = "모집 학년이 누락되었습니다.")
    @Size(max = 4, message = "최대 4개의 학년만 선택할 수 있습니다.")
    @Valid
    private final Set<GradeRequestDto> grades;

    @Valid
    private final PeriodRequestDto period;

    public ProjectCreationRequestDto(
            String title, String content, LocalDateTime deadline,
            ProjectPurpose purpose, MeetingType meetingType,
            Set<PositionInfoCreationRequestDto> positions,
            Set<Skill> skills, Set<GradeRequestDto> grades, PeriodRequestDto period
    ) {
        super(title, content, deadline);
        this.purpose = purpose;
        this.meetingType = meetingType;
        this.positions = positions;
        this.skills = skills;
        this.grades = grades;
        this.period = period;
    }

    public static Project toEntity(User user, ProjectCreationRequestDto dto) {
        Set<Integer> grades = dto.getGrades().stream()
                .map(GradeRequestDto::grade)
                .collect(Collectors.toSet());

        Set<PositionParticipantInfo> positions = dto.getPositions().stream()
                .map(PositionInfoCreationRequestDto::to)
                .collect(Collectors.toSet());

        return Project.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .deadline(dto.getDeadline())
                .purpose(dto.getPurpose())
                .meetingType(dto.getMeetingType())
                .positions(positions)
                .skills(dto.getSkills())
                .grades(grades)
                .period(PeriodRequestDto.to(dto.getPeriod()))
                .build();
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
