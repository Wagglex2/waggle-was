package com.wagglex2.waggle.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectSummaryResponseDto extends BaseRecruitmentSummaryResponseDto {

    private final ProjectPurpose purpose;
    private final MeetingType meetingType;
    private final Set<PositionType> positions;
    private final Set<Skill> skills;

    public ProjectSummaryResponseDto(
            Long id, Long authorId, String authorNickname,
            University university, RecruitmentCategory category, String title,
            LocalDateTime deadline, RecruitmentStatus status,
            ProjectPurpose projectPurpose, MeetingType meetingType,
            Set<PositionType> positions, Set<Skill> skills
    ) {
        super(id, authorId, authorNickname, university, category, title, deadline, status);
        this.purpose = projectPurpose;
        this.meetingType = meetingType;
        this.positions = Set.copyOf(positions);
        this.skills = Set.copyOf(skills);
    }
}
