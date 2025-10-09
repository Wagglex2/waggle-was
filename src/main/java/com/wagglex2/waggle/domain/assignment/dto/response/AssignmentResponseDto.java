package com.wagglex2.waggle.domain.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.ParticipantInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentResponseDto extends BaseRecruitmentResponseDto {
    private final String department;
    private final String lecture;
    private final String lectureCode;
    private final ParticipantInfoResponseDto participants;
    private final Set<Integer> grades;

    private AssignmentResponseDto(
            Long id, Long authorId, String authorNickname, RecruitmentCategory category, University university,
            String title, String content, LocalDateTime deadline, LocalDateTime createdAt,
            RecruitmentStatus status, int viewCount, String department, String lecture, String lectureCode,
            ParticipantInfoResponseDto participants, Set<Integer> grades
    ) {
        super(id, authorId, authorNickname, category, university, title, content, deadline, createdAt, status, viewCount);
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
        this.participants = participants;
        this.grades = grades;
    }

    public static AssignmentResponseDto fromEntity(Assignment assignment) {
        User author = assignment.getUser();
        ParticipantInfoResponseDto participants = ParticipantInfoResponseDto.from(assignment.getParticipants());


        return new AssignmentResponseDto(
                assignment.getId(), author.getId(), author.getNickname(), assignment.getCategory(), author.getUniversity(),
                assignment.getTitle(), assignment.getContent(), assignment.getDeadline(), assignment.getCreatedAt(),
                assignment.getStatus(), assignment.getViewCount(), assignment.getDepartment(), assignment.getLecture(),
                assignment.getLectureCode(), participants, assignment.getGrades()
        );
    }
}
