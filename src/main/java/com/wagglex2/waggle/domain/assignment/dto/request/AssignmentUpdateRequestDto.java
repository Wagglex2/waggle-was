package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.ParticipantInfoUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class AssignmentUpdateRequestDto extends AssignmentCommonRequestDto {

    @NotNull(message = "참여 인원 정보가 누락되었습니다.")
    @Valid
    private final ParticipantInfoUpdateRequestDto participants;

    public AssignmentUpdateRequestDto(
            String title, String content, LocalDateTime deadline,
            String department, String lecture, String lectureCode,
            ParticipantInfoUpdateRequestDto participants, Set<GradeRequestDto> grades
    ) {
        super(title, content, deadline, department, lecture, lectureCode, grades);
        this.participants = participants;
    }
}
