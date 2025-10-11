package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AssignmentCreationRequestDto extends AssignmentCommonRequestDto {


    @NotNull(message = "모집 인원이 누락되었습니다.")
    @Min(value = 1, message = "모집 인원은 1 이상이어야 합니다.")
    private final Integer maxParticipants;

    public AssignmentCreationRequestDto(
            String title, String content, LocalDateTime deadline,
            String department, String lecture, String lectureCode,
            Integer maxParticipants, Set<GradeRequestDto> grades
    ) {
        super(title, content, deadline, department, lecture, lectureCode, grades);
        this.maxParticipants = maxParticipants;
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
                .participants(new ParticipantInfo(dto.getMaxParticipants()))
                .grades(grades)
                .build();
    }
}
