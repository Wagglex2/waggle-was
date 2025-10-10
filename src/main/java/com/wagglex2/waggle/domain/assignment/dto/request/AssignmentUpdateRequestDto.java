package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.common.dto.request.BaseRecruitmentRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public class AssignmentUpdateRequestDto extends BaseRecruitmentRequestDto {

    @NotBlank(message = "학과명이 누락되었습니다.")
    private final String department;

    @NotBlank(message = "과목명이 누락되었습니다.")
    private final String lecture;

    @NotBlank(message = "과목코드가 누락되었습니다.")
    private final String lectureCode;

    @NotNull(message = "모집 인원이 누락되었습니다.")
    @Min(value = 1, message = "모집 인원은 1 이상이어야 합니다.")
    private final Integer maxParticipants;

    @NotEmpty(message = "모집 학년이 누락되었습니다.")
    @Valid
    private final Set<GradeRequestDto> grades;

    public AssignmentUpdateRequestDto(
            String title, String content, LocalDateTime deadline,
            String department, String lecture, String lectureCode,
            Integer maxParticipants, Set<GradeRequestDto> grades
    ) {
        super(title, content, deadline);
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
        this.maxParticipants = maxParticipants;
        this.grades = grades;
    }
}
