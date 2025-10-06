package com.wagglex2.waggle.domain.study.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.request.BaseRecruitmentRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class StudyCreationRequestDto extends BaseRecruitmentRequestDto {
    @NotNull(message = "모집 인원이 누락되었습니다.")
    @Min(value = 1, message = "모집 인원은 1 이상이어야 합니다.")
    private final Integer maxParticipants;

    @Valid
    private final PeriodRequestDto period;

    @NotEmpty(message = "기술 스택이 누락되었습니다.")
    private final Set<Skill> skills;

    protected StudyCreationRequestDto(
            String title, String content, LocalDateTime deadline,
            Integer maxParticipants, PeriodRequestDto period, Set<Skill> skills
    ) {
        super(title, content, deadline);
        this.maxParticipants = maxParticipants;
        this.period = period;
        this.skills = skills;
    }

    public void validate() {
        if (getDeadline().isAfter(period.endDate().atTime(23, 59, 59))) {
            throw new BusinessException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "마감일은 스터디 종료일 이전이어야 합니다."
            );
        }
    }
    public static Study toEntity(User user, StudyCreationRequestDto dto) {
        return Study.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .deadline(dto.getDeadline())
                .participants(new ParticipantInfo(dto.getMaxParticipants()))
                .period(PeriodRequestDto.to(dto.getPeriod()))
                .skills(dto.getSkills())
                .build();
    }
}
