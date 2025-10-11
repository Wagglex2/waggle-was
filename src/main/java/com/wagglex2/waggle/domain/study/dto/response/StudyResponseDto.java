package com.wagglex2.waggle.domain.study.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.ParticipantInfoResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.PeriodResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyResponseDto extends BaseRecruitmentResponseDto {
    private final ParticipantInfoResponseDto participants;
    private final PeriodResponseDto period;

    @Setter
    private Set<Skill> skills;

    private StudyResponseDto(
            Long id, Long authorId, String authorNickname, RecruitmentCategory category, University university,
            String title, String content, LocalDateTime deadline, LocalDateTime createdAt,
            RecruitmentStatus status, int viewCount,
            ParticipantInfoResponseDto participants, PeriodResponseDto period
    ) {
        super(id, authorId, authorNickname, category, university, title, content, deadline, createdAt, status, viewCount);
        this.participants = participants;
        this.period = period;
    }

    public static StudyResponseDto fromEntity(Study study) {
        User author = study.getUser();
        ParticipantInfoResponseDto participants = ParticipantInfoResponseDto.from(study.getParticipants());
        PeriodResponseDto period = PeriodResponseDto.from(study.getPeriod());


        return new StudyResponseDto(
                study.getId(), author.getId(), author.getNickname(), study.getCategory(), author.getUniversity(),
                study.getTitle(), study.getContent(), study.getDeadline(), study.getCreatedAt(),
                study.getStatus(), study.getViewCount(), participants, period
        );
    }
}