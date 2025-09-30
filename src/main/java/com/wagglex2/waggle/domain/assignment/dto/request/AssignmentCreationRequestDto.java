package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.dto.request.BaseRecruitmentRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AssignmentCreationRequestDto extends BaseRecruitmentRequestDto {
    @NotBlank(message = "학과명이 누락되었습니다.")
    private final String department;

    @NotBlank(message = "과목명이 누락되었습니다.")
    private final String lecture;

    @NotBlank(message = "과목코드가 누락되었습니다.")
    private final String lectureCode;

    @NotNull(message = "모집 인원이 누락되었습니다.")
    @Valid
    private final ParticipantInfo participants;

    @NotEmpty(message = "모집 학년이 누락되었습니다.")
    @Valid
    private final Set<GradeRequestDto> grades;

    public AssignmentCreationRequestDto(
            String title, String content, LocalDateTime deadline,
            String department, String lecture, String lectureCode,
            ParticipantInfo participants, Set<GradeRequestDto> grades
    ) {
        super(title, content, deadline);
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
        this.participants = participants;
        this.grades = grades;
    }

    public static Assignment toEntity(User user, AssignmentCreationRequestDto dto) {
        Set<Integer> grades = dto.getGrades().stream()
                .map(GradeRequestDto::grade)
                .collect(Collectors.toSet());

        return Assignment.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .deadline(dto.getDeadline())
                .department(dto.getDepartment())
                .lecture(dto.getLecture())
                .lectureCode(dto.getLectureCode())
                .participants(dto.getParticipants())
                .grades(grades)
                .build();
    }
}
