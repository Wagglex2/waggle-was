package com.wagglex2.waggle.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.PeriodResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.PositionInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponseDto extends BaseRecruitmentResponseDto {
    private final ProjectPurpose purpose;
    private final MeetingType meetingType;
    private final Set<PositionInfoResponseDto> positions;
    private final Set<Skill> skills;
    private final Set<Integer> grades;
    private final PeriodResponseDto period;

    private ProjectResponseDto(
            Long id, Long authorId, String authorNickname, RecruitmentCategory category, University university,
            String title, String content, LocalDateTime deadline, LocalDateTime createdAt,
            RecruitmentStatus status, int viewCount, ProjectPurpose purpose, MeetingType meetingType,
            Set<PositionInfoResponseDto> positions, Set<Skill> skills, Set<Integer> grades, PeriodResponseDto period
    ) {
        super(id, authorId, authorNickname, category, university, title, content, deadline, createdAt, status, viewCount);
        this.purpose = purpose;
        this.meetingType = meetingType;
        this.positions = positions;
        this.skills = skills;
        this.grades = grades;
        this.period = period;
    }

    public static ProjectResponseDto fromEntity(Project project) {
        User author = project.getUser();
        PeriodResponseDto period = PeriodResponseDto.from(project.getPeriod());
        Set<PositionInfoResponseDto> positions = project.getPositions().stream()
                .map(PositionInfoResponseDto::from)
                .collect(Collectors.toSet());

        return new ProjectResponseDto(
                project.getId(), author.getId(), author.getNickname(), project.getCategory(), author.getUniversity(),
                project.getTitle(), project.getContent(), project.getDeadline(), project.getCreatedAt(),
                project.getStatus(), project.getViewCount(), project.getPurpose(), project.getMeetingType(),
                positions, project.getSkills(), project.getGrades(), period
        );
    }
}
