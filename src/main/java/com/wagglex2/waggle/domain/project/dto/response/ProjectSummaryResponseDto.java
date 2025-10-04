package com.wagglex2.waggle.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.PositionInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectSummaryResponseDto extends BaseRecruitmentSummaryResponseDto {

    private final ProjectPurpose projectPurpose;
    private final MeetingType meetingType;
    private final PositionInfoResponseDto positions;
    private final Set<Skill> skills;

    protected ProjectSummaryResponseDto(
            Long id, Long authorId, String authorNickname,
            RecruitmentCategory category, University university, String title,
            LocalDateTime deadline, RecruitmentStatus status,
            ProjectPurpose projectPurpose, MeetingType meetingType,
            PositionInfoResponseDto positions, Set<Skill> skills
    ) {
        super(id, authorId, authorNickname, category, university, title, deadline, status);
        this.projectPurpose = projectPurpose;
        this.meetingType = meetingType;
        this.positions = positions;
        this.skills = skills;
    }
}
