package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.common.type.RecruitmentParticipants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class AssignmentRequestDto {
    // private Long id;
    // private User user;
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    @Future
    private LocalDateTime deadline;
    // private RecruitmentCategory category;
    // private RecruitmentStatus status;
    // private LocalDateTime createdAt;
    // private int viewCount;
    @NotBlank
    private String department;

    @NotBlank
    private String lecture;

    @NotBlank
    private String lectureCode;

    @NotNull
    @Valid
    private RecruitmentParticipants participants;

    @NotEmpty
    private List<Integer> grades;
}
