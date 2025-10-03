package com.wagglex2.waggle.domain.project.dto.request;

import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PositionInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class ProjectUpdateRequestDto extends ProjectCommonRequestDto {

    @NotEmpty(message = "포지션 정보가 누락되었습니다.")
    @Valid
    private final Set<PositionInfoUpdateRequestDto> positions;

    public ProjectUpdateRequestDto(
            String title, String content, LocalDateTime deadline,
            ProjectPurpose purpose, MeetingType meetingType, Set<Skill> skills,
            Set<GradeRequestDto> grades, PeriodRequestDto period,
            Set<PositionInfoUpdateRequestDto> positions
    ) {
        super(title, content, deadline, purpose, meetingType, skills, grades, period);
        this.positions = positions;
    }
}
