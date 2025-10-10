package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

public class AssignmentUpdateRequestDto extends AssignmentCommonRequestDto {
    public AssignmentUpdateRequestDto(
            String title, String content, LocalDateTime deadline,
            String department, String lecture, String lectureCode,
            Integer maxParticipants, Set<GradeRequestDto> grades
    ) {
        super(title, content, deadline, department, lecture, lectureCode, maxParticipants, grades);
    }
}
